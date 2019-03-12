package com.example.credhubber.controller;

import com.example.credhubber.domain.Secret;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.credhub.core.CredHubOperations;
import org.springframework.credhub.support.CredentialDetails;
import org.springframework.credhub.support.SimpleCredentialName;
import org.springframework.credhub.support.json.JsonCredential;
import org.springframework.credhub.support.json.JsonCredentialRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping(path = "/write", consumes = "application/json", produces = "application/json")
    public String writeCredential( @RequestBody Secret secret ) {

            Map<String, Object> value = new HashMap<>();
            value.put( "foo", "bar");

            try {
                JsonCredentialRequest request = JsonCredentialRequest.builder()
                        .name( new SimpleCredentialName( secret.name ) )
                        .value( value ).build();

                CredentialDetails<JsonCredential> credentialDetails = credHubOperations.credentials().write( request );
                System.out.println( "Successfully wrote credentials: " +  credentialDetails);

                return "done";
            }
            catch (Exception e) {
                System.out.println( "Error writing credentials: " + e.getMessage() );
                return "FAILED : " + e.getMessage();
            }
    }
}
