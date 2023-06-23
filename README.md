# System Description
The system has been divided into three microservices and a Kafka cluster.

![](https://imgur.com/E8YlfL8.jpg)

## Microservices
### ApiGatewayService
* Responsible for receiving and processing all API requests.
* Provides a unified API entry point to coordinate and forward requests to the appropriate services.
* Forwards requests to Kafka using three producers.

### AuthService
* OAuth service for login and token authentication.
* Handles identity authentication and permission control-related business logic.

### TransactionManagerService
* Service responsible for handling the core system logic.
* Manages business logic related to transactions.
* Consumes messages from Kafka using three consumers. 
* Sends processed results back to Kafka.

## Kafka Cluster
The requests are processed using a Kafka cluster, where each broker has 3 partitions for horizontal expansion.
(The reason for using 3 brokers ensures proper leader partition election.)


### Kafka Querying using KsqlDB
ksqlDB is a database specifically designed for building streaming applications on top of Apache Kafka.
It is a tool within the Kafka ecosystem that allows us to process Kafka topics similar to traditional tables in a relational database and perform SQL-like queries on them. 
Here, I use it to query transaction records within Kafka.


### Exchange Rate API
Choose the Exchange rate API (https://exchangeratesapi.io/).

<font size=4.5 color="#ee496a">**NOTE:**</font> Due to the limitations of the free plan, currently only <font size=3.5 color="#ee496a">EUR</font> can be used as the base currency to query exchange rates against other currencies.



---

## Future Work

Due to time constraints, the following points have not been addressed to perfection, 
but they have been documented for future completion:

* Strengthen the security authentication for each service:
Currently, OAuth authentication is only implemented at the API layer. 
In the future, encryption for communication can be added to all services.
* Transition from plaintext transmission in Brokers:
Due to time constraints, plaintext transmission is currently used in Brokers for faster development. 
It should be encryption in the future.
* Improve CI/CD pipeline and K8S:
The configuration of CircleCI and Kubernetes (K8S) for continuous integration and continuous deployment (CI/CD) is still incomplete and requires further refinement.


# My experience
This is my first time delving into another facet of Kafka, specifically its『data storage functionality.』
In the past, my experience with Kafka was primarily centered around using it as a messaging queue mechanism. Therefore, in this project, I've been learning and implementing KsqlDB along the way. 
This project has been a great learning experience. Although time limitations prevented me from achieving perfection in the KsqlDB portion, 
I am determined to continue working on and completing this project. Until now, I still keep working on this project

I'd like to express my sincere gratitude to the Sympulse team for giving me this opportunity to take this challenge.