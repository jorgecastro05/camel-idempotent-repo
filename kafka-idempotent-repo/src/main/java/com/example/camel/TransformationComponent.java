package com.example.camel;

import org.apache.camel.Exchange;
import org.apache.camel.language.Simple;
import org.apache.camel.processor.idempotent.kafka.KafkaIdempotentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransformationComponent {

    @Autowired
    private KafkaIdempotentRepository kafkaIdempotentRepository;

    public void markEntryAsFailed(@Simple("${body[id]}") String repoKey){
        kafkaIdempotentRepository.remove(repoKey);
    }
}
