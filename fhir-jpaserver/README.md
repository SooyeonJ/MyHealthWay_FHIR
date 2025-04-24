# HAPI-FHIR Starter Project

해당 프로젝트는 HAPI FHIR JPA Clone 프로젝트입니다.

- Need Help? Please see: https://github.com/hapifhir/hapi-fhir/wiki/Getting-Help
- Original Github : https://github.com/hapifhir/hapi-fhir-jpaserver-starter
- Documentation : https://hapifhir.io/hapi-fhir/docs/server_jpa

## Prerequisites

In order to use this sample, you should have:

- [This project](https://github.com/MegaBridgeDev/hapi-fhir-jpaserver) checked out. You may wish to create a GitHub Fork of the project and check that out instead so that you can customize the project and save the results to GitHub.
- 오라클 자바 (JDK) installed: Minimum JDK17.
- 아파치 메이븐 빌드 툴 (newest version)
- 데이터베이스 (H2, Mysql, PostGreSQL)

## Running locally

The easiest way to run this server entirely depends on your environment requirements. At least, the following 4 ways are supported:

### Using Spring Boot with :run
```bash
mvn clean spring-boot:run -Pboot
```
Server will then be accessible at http://localhost:8080/ and eg. http://localhost:8080/fhir/metadata. Remember to adjust you overlay configuration in the application.yaml to eg.

```yaml
    tester:
      -
          id: home
          name: Local Tester
          server_address: 'http://localhost:8080/fhir'
          refuse_to_fetch_third_party_urls: false
          fhir_version: R4
```

## Configurations
기본설정으로는 H2로 설정되어있으며, http://h2database.com/html/main.html 해당 사이트에서 H2를 설치하여야 실행 가능. 별도의 PostgreSQL 설정 필요
수정이 필요 yaml file _src/main/resources/application.yaml_.

### PostgreSQL configuration

To configure the starter app to use PostgreSQL, instead of the default H2, update the application.yaml file to have the following:

```yaml
spring:
  datasource:
    url: 'jdbc:postgresql://localhost:5432/hapi_dstu3'
    username: admin
    password: admin
    driverClassName: org.postgresql.Driver
```


Because the integration tests within the project rely on the default H2 database configuration, it is important to either explicity skip the integration tests during the build process, i.e., `mvn install -DskipTests`, or delete the tests altogether. Failure to skip or delete the tests once you've configured PostgreSQL for the datasource.driver, datasource.url, and hibernate.dialect as outlined above will result in build errors and compilation failure.


### Authentication Mode

To Turn on the Authentication mode to use OAuth2 Token, instead of the deafult Unsecure mode, update the StarterJpaConfig.java  :
```
   ca.uhn.fhir.jpa.starter.common.StartJpaConfig.java
   
   public RestfulServer restfulServer(
   ....
   fhirServer.registerInterceptor(authorizationInterceptor);
   ....
   )
```