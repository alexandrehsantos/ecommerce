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
git clone https://gitlab.com/youruser/sham
```

- Go to the project directory:

```
cd sham
```

Configuration fiel
```
Update the information on application-local.yml
```
Profile = local
```
By vm options
Set the profile at vm options -Dspring.profiles.active=local
```

```
By maven plugin
mvn spring-boot:run -Dspring-boot.run.profiles=local 
```
```
By STS (SpringToolSuite)
Execute o Atalho Ctrl+ALT+P 
```
```
By IntelliJ Ultimate (SpringToolSuite)
Type the profile name local at active profiles.
```
```
By IntelliJ Community (SpringToolSuite)
Type bellow build and run --spring.profiles.active=local
```


- Project build:

```
mvn clean install at root folder
```

```
mvn spring-boot:run
```

## Instructions to use the API are under construction. 