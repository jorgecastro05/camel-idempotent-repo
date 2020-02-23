package com.example.camel;

import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "repository")
public class IdempotentRepoConfiguration {

    private String host;
    private Integer port;
    private String user;
    private String password;


    @Bean(initMethod = "start", destroyMethod = "stop")
    public RemoteCacheManager cacheManager() {
        ConfigurationBuilder clientBuilder = new ConfigurationBuilder();
        clientBuilder
                .addServer()
                .host(host)
                .port(port)
                .security().authentication()
                .enable()
                .saslMechanism("DIGEST-MD5")
                .callbackHandler(new MyCallbackHandler(user, "ApplicationRealm", password.toCharArray()));
        RemoteCacheManager remoteCacheManager = new RemoteCacheManager(clientBuilder.build());
        return remoteCacheManager;
    }

    @Bean
    public InfinispanIdempotentRepository infinispanIdempotentRepository() {
        InfinispanIdempotentRepository infinispanIdempotentRepository = new InfinispanIdempotentRepository(cacheManager(), "idempotent");
        return infinispanIdempotentRepository;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
