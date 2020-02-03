package com.example.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class MySpringBootRouter extends RouteBuilder {


    @Override
    public void configure() {

        super.getContext().setStreamCaching(true);
        super.getContext().setUseMDCLogging(true);


        /*
        This example forces the second consumer throw an exception, but the entries are rollback at the end of split
        This assures that first consumer won't take the same failed entry in the execution time
        */

        onException(IllegalStateException.class)
                .continued(true)
                .log("The entry has failed: ${exception.message}")
                .bean("transformationComponent", "markEntryAsFailed")
                .end();


        from("timer:initCars1?period=15000").routeId("consume-cars-worker-1")
                .log("Init route consume cars database ${id}")
                .to("sql:select * from CARS?dataSource=#dataSource")
                .log("number of results returned: ${body.size()}")
                .split(body())
                .idempotentConsumer(simple("${body[id]}")).messageIdRepositoryRef("kafkaIdempotentRepository")
                .delay(simple("${random(100,500)}"))//simulate a process with the entry
                .log("Car processed: ${body}")
                .end();

        from("timer:initCars2?period=15000").routeId("consume-cars-worker-2")
                .log("Init route consume cars database ${id}")
                .to("sql:select * from CARS?dataSource=#dataSource")
                .log("number of results returned: ${body.size()}")
                .setProperty("failedEntries").exchange(ex -> new ArrayList<String>())
                .split(body())
                .idempotentConsumer(simple("${body[id]}")).messageIdRepositoryRef("kafkaIdempotentRepository")
                .delay(simple("${random(100,500)}")) //simulate a process with the entry
                .log("Car processed: ${body}")
                .throwException(IllegalStateException.class, "The Car has an error")
                .end() //delay
                .end() //idempotent
                .end() //split
                .log("Rollback for all failed entries ${exchangeProperty.failedEntries}")
                .delay(1000)
                .bean("transformationComponent", "rollbackFailedEntries")
                .end(); //final end


    }

}
