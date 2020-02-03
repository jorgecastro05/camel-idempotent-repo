**Camel example using idempotent repository**

This example uses the camel EIP Idempotent Consumer to query entries in database simulating two concurrent consumers,
processing one entry at time, uses a kafka and infispan server as idempotent repository.

* Consumer one takes all messages and prints.
* Consumer two takes all messages and simulates an error on processing, removing the key in the repo.

The route consumers starts at the same time.