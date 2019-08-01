# Recruitment Application
HTTP service that serves API's for recruitment.

### Prerequisites -
```
 - Java8
 - maven (optional)
```

### Used framework and libraries-
```
 - Spring boot
 - Spring data JPA
 - H2database
```

### Set Up -
 Unzip this folder -

 This will create 'recruitment-heavenhr-interview' directory in directory where its extracted then go to this 'recruitment-heavenhr-interview' directory
```
 cd recruitment-heavenhr-interview
```
 You can see following files in this directory
```
 - pom.xml
 - README.md
 - mvnw
 - mvnw.cmd
 - src
```
 Run following mvn command to download dependencies and generating build.
```
mvn clean install OR ./mvnw clean install (if maven is installed on system)

```
 This will download all the dependencies and will generate the executable jar in target directory.
 Jar with name 'recruitment-0.0.1-SNAPSHOT.jar' will be present in target directory.

### Excecution -
After done with primary setup you can start the execution of the application with this command -
```
mvn spring-boot:run OR ./mvnw spring-boot:run (if maven is installed on system)
```
This will start the application on 8080 port.

### Usage -
In total, there are total eight exposed APIs and working of which is explained with curl command -

#### Create offer - POST - /api/v1/offers
```
Request - curl -H "Content-Type: application/json" -X POST -d '{"jobTitle":"test", "startDate":"2019-10-10"}' http://localhost:8080/api/v1/offers
Response - {"id":1,"jobTitle":"test","startDate":"2019-10-10"}
```

#### Get offer by id - GET - /api/v1/offers/{offer_id}
```
Request - curl -H "Content-Type: application/json" -X GET http://localhost:8080/api/v1/offers/1
Response - {"id":1,"jobTitle":"test","startDate":"2019-10-10"}
```

#### Get all offers - GET - /api/v1/offers
```
Request - curl -H "Content-Type: application/json" -X GET http://localhost:8080/api/v1/offers
Response - {"offers":[{"id":1,"jobTitle":"test","startDate":"2019-10-10"}]}
```

#### Create application - POST - /api/v1/applications
```
Request - curl -H "Content-Type: application/json" -X POST -d '{"offerId":1, "email":"abc@test.com", "resumeText":"axcdddd"}' http://localhost:8080/api/v1/applications
Response - {"id":1,"email":"abc@test.com","resumeText":"axcdddd","status":"APPLIED","offer":{"id":1,"jobTitle":"test","startDate":"2019-10-10"}}
```

#### Get application by offer id and application id - GET - /api/v1/offers/{offer_id}/applications/{application_id}
```
Request -  curl -H "Content-Type: application/json" -X GET http://localhost:8080/api/v1/offers/1/applications/1
Response - {"id":1,"email":"abc@test.com","resumeText":"axcdddd","status":"APPLIED","offer":{"id":1,"jobTitle":"test","startDate":"2019-10-10"}}
```

#### Get all applications by offer id - GET - /api/v1/offers/{offer_id}/applications
```
Request -  curl -H "Content-Type: application/json" -X GET http://localhost:8080/api/v1/offers/1/applications
Response - {"applications":[{"id":1,"email":"abc@test.com","resumeText":"axcdddd","status":"APPLIED","offer":{"id":1,"jobTitle":"test","startDate":"2019-10-10"}}]}
```

#### Get applications count by offer id - GET - /api/v1/offers/{offer_id}/applications/count
```
Request -  curl -H "Content-Type: application/json" -X GET http://localhost:8080/api/v1/offers/1/applications/count
Response - {"noOfApplications":1}
```

#### Update application status - PUT - /api/v1/offers/applications/{application_id}
```
Request -  curl -H "Content-Type: application/json" -X PUT -d '{"status":"INVITED"}' http://localhost:8080/api/v1/applications/1
Response - {"id":1,"email":"abc@test.com","resumeText":"axcdddd","status":"INVITED","offer":{"id":1,"jobTitle":"test","startDate":"2019-10-10"}}
```

