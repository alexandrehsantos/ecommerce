# Ecommerce 

This project is sample implementation using Kafka and pure Java.  


## Starting

The following instructions are for the project execution in local machine, for developing and tests.


### Requirements

Is necessary to have the requirements bellow.

```
- Java 18;
- Git;
- Maven 3.8.6;
- Kafka;
```

### Instation

- Clone the project:

```
git clone  https://github.com/youuser/ecommerce.git
```

- Go to the project directory:

```
- cd ecommerce
```
```
- mvn clean install at root folder
```


### Help scripts for Kafka
```
For auxiliary in the start and stop Kafka there are two scripts inside src/main/resources/:
    start-kafka-enviroment.sh
    stop-kafka-enviroment.sh
Just change kafka_server_location, zookeeper_location, config_path variables to your envrironment.
```
