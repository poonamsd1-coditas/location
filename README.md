
# Location Application Overview:-
The application is a location search engine that gives a list of venues that match the search query,
and also allows filtering as per category or type.

# Installations required:-
* Git(https://www.atlassian.com/git/tutorials/install-git)
* Maven(https://maven.apache.org/download.cgi)
* Java 1.8(https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* Postman 7.7.2(https://www.getpostman.com/downloads/)

Clone the repository on your local machine and run the following commands to start the application:
##### Build the application without test-cases
``` mvn clean install -DskipTests```
##### Run the application
``` mvn spring-boot:run```
##### Run tests
``` mvn test```

# Server port: 8090

# API endpoint:-
GET : http://localhost:8089/api/location/getLocation?query={query}?filter={filter}
* {query}(required) = Search string to find location e.g. Pune
* {filter}(optional) = Category/type to filter out the search results e.g. bank

# Sample output :-
The following URL can be tested on Postman and the following results can be obtained

GET : http://localhost:8089/api/location/getLocation?query=pune?filter=bank

{
    "status": "OK",
    "message": "Locations have been populated",
    "locations": [
        {
            "name": "Kotak Mahindra Bank",
            "city": "Pune",
            "state": "Mahārāshtra",
            "country": "India",
            "countryCode": "IN",
            "postalCode": "411011",
            "address": "Shop No.1, Kasba Peth,  Bangare Apt (Opp Shaniwarwada) Pune 411011 Mahārāshtra India ",
            "lat": "18.519552",
            "lng": "73.856328",
            "category": "Bank"
        },
        {
            "name": "Paud Road",
            "city": "Pune",
            "state": "Maharashtra",
            "country": "India",
            "countryCode": "IN",
            "postalCode": "411038",
            "address": "1-B,S.NO.119/1, Anant Krupa, Paud Rd, Jhala Co-Op Housing Society, Kothrud, Pune, Maharashtra 411038, India",
            "lat": "18.5082765",
            "lng": "73.80824179999999",
            "category": "bank, establishment, finance, point_of_interest"
        }
    ]
}



