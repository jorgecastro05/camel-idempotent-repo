package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * A simple Camel route that triggers from a timer and calls a bean and prints to system out.
 * <p/>
 * Use <tt>@Component</tt> to make Camel auto detect this route when starting.
 */
@Component
public class MySpringBootRouter extends RouteBuilder {


    @Override
    public void configure() {

        super.getContext().setStreamCaching(true);
        super.getContext().setUseMDCLogging(true);

        from("timer:initCars1?period=5000").routeId("consume-cars-database-1")
                .log("Init route consume cars database ${id}")
                .to("sql:select * from CARS?dataSource=#dataSource")
                .log("number of results returned: ${body.size()}")
                .split(body())
                .idempotentConsumer(simple("${body[id]}")).messageIdRepositoryRef("infinispanIdempotentRepository")
                .delay(simple("${random(100,500)}")) //simulate a process with the entry
                .log("Car found: ${body}")
                .end();

        from("timer:initCars2?period=5000").routeId("consume-cars-database-2")
                .log("Init route consume cars database ${id}")
                .to("sql:select * from CARS?dataSource=#dataSource")
                .log("number of results returned: ${body.size()}")
                .split(body())
                .idempotentConsumer(simple("${body[id]}")).messageIdRepositoryRef("infinispanIdempotentRepository")
                .delay(simple("${random(100,500)}")) //simulate a process with the entry
                .log("Car found: ${body}")
                .bean("transformationComponent","markEntryAsFailed" )
                .end();

    }

}
