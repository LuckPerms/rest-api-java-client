# LuckPerms REST API - Java Client

The [LuckPerms REST API](https://github.com/LuckPerms/rest-api) allows developers to query LuckPerms data using HTTP calls.

This library allows developers to make these HTTP calls easily using a familiar Java API. :)

### Usage

Add a dependency to your Maven/Gradle buildscript:

e.g.

```groovy
compileOnly 'net.luckperms:rest-api-client:0.1'
```

```xml
<dependency>
    <groupId>net.luckperms</groupId>
    <artifactId>rest-api-client</artifactId>
    <version>0.1</version>
</dependency>
```

Then, create a new client and start making calls:

```java
LuckPermsClient client = LuckPermsClient.builder()
        .baseUrl("http://localhost:8080")
        .apiKey("abc123")
        .build();
        
Response<Group> response = client.groups().get("admin").execute();
```
