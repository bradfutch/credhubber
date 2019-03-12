package com.example.credhubber.controller;

import com.example.credhubber.domain.Secret;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
public class CredhubberController {

    public CredHubOperations credHubOperations;

    public CredhubberController(CredHubOperations credHubOperations) {
        this.credHubOperations = credHubOperations;
    }

    @GetMapping("/hello")
    public String hello() {
        return "hi";
    }

    @JsonAnyGetter
    @PostMapping(path = "/write", consumes = "application/json", produces = "application/json")
    public Map<String, Object> writeCredential( @RequestBody Secret cred ) {

        try {
            JsonCredentialRequest request = JsonCredentialRequest.builder()
                    .name(new SimpleCredentialName(cred.name))
                    .value(cred.value).build();

            CredentialDetails<JsonCredential> credentialDetails = credHubOperations.credentials().write(request);
            System.out.println("Successfully wrote credentials: " + credentialDetails);

            return credentialDetails.getValue();
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
