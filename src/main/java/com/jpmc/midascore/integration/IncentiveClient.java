package com.jpmc.midascore.integration;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IncentiveClient {

    private final RestTemplate rest;

    // The task says the service runs on localhost:8080 and exposes /incentive
    private static final String URL = "http://localhost:8080/incentive";

    public IncentiveClient(RestTemplateBuilder builder) {
        this.rest = builder.build();
    }

    public Incentive fetch(Transaction tx) {
        // Let Spring/Jackson serialize Transaction and deserialize Incentive
        return rest.postForObject(URL, tx, Incentive.class);
    }
}
