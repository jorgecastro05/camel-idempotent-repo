package com.example.camel;

import org.apache.camel.component.infinispan.processor.idempotent.InfinispanIdempotentRepository;
import org.infinispan.manager.DefaultCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class BeansConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    public DefaultCacheManager cacheManager() {
        DefaultCacheManager cacheManager = new DefaultCacheManager();
        return cacheManager;
    }

    @Bean
    public InfinispanIdempotentRepository infinispanIdempotentRepository() {
        InfinispanIdempotentRepository infinispanIdempotentRepository = new InfinispanIdempotentRepository(cacheManager(), "idempotent");
        return infinispanIdempotentRepository;
    }


    @Bean(name = "dataSource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:~/myDB;MV_STORE=false");
        dataSource.setUsername("sa");
        dataSource.setPassword("");

        // schema init
        Resource initData = new ClassPathResource("cars.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        return dataSource;
    }
}
