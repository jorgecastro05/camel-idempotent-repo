**Camel example using idempotent repository**

This example uses the camel EIP Idempotent Consumer to query entries in database simulating two concurrent consumers,
processing one entry at time, uses a kafka and infispan server as idempotent repository.

* Consumer one takes all messages and prints.
* Consumer two takes all messages and simulates an error on processing, removing the key in the repo.

The route consumers starts at the same time.

Create App and deploy to openshift
oc new-app fabric8/s2i-java~https://github.com/jorgecastro05/camel-idempotent-repo --context-dir infispan-idempotent-repo