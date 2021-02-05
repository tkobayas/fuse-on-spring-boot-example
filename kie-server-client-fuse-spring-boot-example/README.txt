### kie-server-client-fuse-spring-boot-example

Steps to test

1. Start RHDM 7.9.0 on EAP with port offset 100 so 8180 is used for http (kie-server is required. decision-central is not required)

2. Build example-kjar
  $ cd example-kjar
  $ mvn clean install

3. Deploy example-kjar to the kie-server
  Run CreateContainer.java in spring-boot-app. Or you can deploy it with REST API.

4. Build spring-boot-app
  $ cd spring-boot-app
  $ mvn clean package

5. Run spring-boot-app
  $ mvn spring-boot:run

6. Sent a request
~~~
curl -X POST -H 'Accept: application/json' -H 'Content-Type: application/json' -d '{"name":"John","age":25}' http://localhost:8080/my-app/canDrink
~~~

The response shows canDrink=true
~~~
{"name":"John","age":25,"canDrink":true}
~~~

