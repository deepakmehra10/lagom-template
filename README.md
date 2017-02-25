# lagom-template
Building Reactive Java 8 application with Lagom framework. This is a classic CRUD application which persist events in Cassandra Db. Here we are using embedded Cassandra to persist events and embedded kafka for publishing and subscribing between microservices.

# Prerequisites
1. Java 1.8
2. Maven 4.0

# Getting the Project
https://github.com/deepakmehra10/lagom-template.git

####Create executable jar: 
`mvn package -Dmaven.skip.test=true`

####Command to start the project

`mvn lagom:runAll`



