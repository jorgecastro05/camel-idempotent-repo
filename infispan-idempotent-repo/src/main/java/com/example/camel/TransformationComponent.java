package com.example.camel;

import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.apache.camel.language.Simple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransformationComponent {

    @Autowired
    private InfinispanIdempotentRepository infinispanIdempotentRepository;

    public void markEntryAsFailed(@Simple("${body[id]}") String repoKey){
        infinispanIdempotentRepository.remove(repoKey);
    }
}
