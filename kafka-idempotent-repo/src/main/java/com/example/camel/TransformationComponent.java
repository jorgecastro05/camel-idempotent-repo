package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.language.Simple;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransformationComponent {

    @Autowired
    private KafkaIdempotentRepository kafkaIdempotentRepository;

    public void markEntryAsFailed(@Simple("${body[id]}") String repoKey, Exchange exchange) {
        List<String> failedEntries = exchange.getProperty("failedEntries", List.class);
        failedEntries.add(repoKey);
    }

    public void rollbackFailedEntries(Exchange exchange) {
        List<String> failedEntries = exchange.getProperty("failedEntries", List.class);
        for (String failedEntry : failedEntries) {
            kafkaIdempotentRepository.remove(failedEntry);
        }
    }

}
