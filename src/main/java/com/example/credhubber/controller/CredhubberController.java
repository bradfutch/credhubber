package com.example.credhubber.controller;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class CredhubberController {

    public CredHubOperations credHubOperations;

    public CredhubberController(CredHubOperations credHubOperations) {
        this.credHubOperations = credHubOperations;
    }

    @JsonAnyGetter
    @GetMapping("/secret")
    public String getCredentialbyPath() {

        try {

            JSONObject vcap = new JSONObject(System.getenv().get( "VCAP_SERVICES" ));
            JSONArray credhub = vcap.getJSONArray( "credhub" );
            JSONObject credentials = credhub.getJSONObject(0).getJSONObject("credentials");

            return credentials.toString();

        } catch (Exception e) {

            System.out.println("Error writing credentials: " + e.getMessage());
            return e.getMessage();
        }

    }

    @JsonAnyGetter
    @PostMapping(path = "/write/{name}", consumes = "application/json", produces = "application/json")
    public Map<String, Object> writeCredential( @PathVariable String name, @RequestBody Map<String, Object> cred ) {

        try {

            JsonCredentialRequest request = JsonCredentialRequest.builder()
                    .name(new SimpleCredentialName(name))
                    .value(cred).build();

            CredentialDetails<JsonCredential> credentialDetails = credHubOperations.credentials().write(request);
            System.out.println("Successfully wrote credentials: " + credentialDetails);

            Map<String, Object> output = new HashMap<String, Object>();
            output.put( "id", credentialDetails.getId() );
            output.put( "value", credentialDetails.getValue() );

            return output;

        } catch (Exception e) {

            System.out.println("Error writing credentials: " + e.getMessage());
            Map<String,Object> thing =  new HashMap<String,Object>();
            thing.put("failed", e.getMessage());
            return thing;
        }
    }

    @JsonAnyGetter
    @GetMapping("/fetch/{id}")
    public Map<String,Object> getCredential(@PathVariable String id) {

        try {

            CredentialDetails<JsonCredential> retrievedDetails =
                    credHubOperations.credentials().getById(id, JsonCredential.class);

            System.out.println("Successfully retrieved credentials by ID: " + retrievedDetails);

            return retrievedDetails.getValue();

        } catch (Exception e) {

            System.out.println("Error retrieving credentials by ID: " + e.getMessage());
            Map<String,Object> thing =  new HashMap<String,Object>();
            thing.put("failed", e.getMessage());
            return thing;
        }
    }
}
