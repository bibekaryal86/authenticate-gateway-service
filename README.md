# authenticate-gateway

This app is a simple implementation of Spring Cloud Gateway to route calls from SPAs to microservices. It was
implemented to overcome CORS issue with a React based SPA, however, microservices might also route their request via the
gateway if required.

The app also functions as an authentication server to validate user credentials. It provides two services on its own:

* Validate username / password and return user details with token to use in subsequent service calls
* Create new user

This is very similar to `pets-gateway` found here: https://github.com/bibekaryal86/pets-gateway, and routes
the services included in that gateway, however, this `authenticate-gateway` is intended for all personal projects moving
forward to handle the CORS error.

The authentication credentials are stored in MongoDB Atlas, however, this app does not use `MongoTemplate` or `JPA`
libraries. Instead, this app utilizes MongoDB's Data API (https://docs.atlas.mongodb.com/api/data-api-resources/) to
insert/retrieve data using HTTPS calls using `RestTemplate`.

To run the app, we need to supply the following environment variables:

* Active Profile
    * SPRING_PROFILES_ACTIVE (development, docker, production)
* Port
    * This is optional, if not provided, it defaults to 8080
    * Required port 5000 (NGINX) in AWS Elastic Beanstalk
* MongoDB Data API Details
    * APPID: Application ID for MongoDB API
    * APIKEY: MongoDB Database API Key
    * DATASOURCE: MongoDB Datasource (same value is used for Database name and Collection name in the system)
* Authentication Details of Routing Services
    * `pets-database` Auth Details
        * PD_USR: Basic Auth Username
        * PD_PWD: Basic Auth Password
    * `pets-service` Auth Details
        * PS_USR: Basic Auth Username
        * PS_PWD: Basic Auth Password
    * `pets-authenticate` Auth Details
        * This app does not have security
    * `health-data-java` Auth Details
        * HDT_USR: Basic Auth Username
        * HDT_PWD: Basic Auth Password

This is the first app that I deployed to Azure. Azure has a good free-tier that should suffice the app's needs
(greater than 256 MB RAM which is not possible with GCP free-tier)

* App Test Link:
    * Azure: https://gatewayauth.azurewebsites.net/authenticate-gateway/tests/ping

In order to deploy to Azure, two things are added to `build.gradle` - the `azure.webapp` plugin and
`azurewebapp` script. Then deploy the app using `gradle azureWebAppDeploy` from the root folder.
