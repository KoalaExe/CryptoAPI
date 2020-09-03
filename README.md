# CryptoAPI
An API built with Spring Boot that uses CoinGecko (https://www.coingecko.com/api/documentations/v3#/) as an external data source.

This repository comes with both a live data build and an API testing build which run on Docker

### Live Data Build
##### Instructions
1. Make sure you are in the root directory and the current branch is ```dev```
2. Run ```cd api-backend``` and then ```mvn clean install``` to build the target ```jar``` file
3. Return to the root directory and then run ```docker-compose up --build -d``` to build the images and run the API on ```http://localhost:8080```
4. Stop the API by running ```http://docker-compose down```

##### API Endpoints
1. ```/coins/{currencyID}```, where currencyID is id of a valid cryptocurrency
2. ```/coins/markets?vs_currency={currencyID}&limit={paginationLimit}&page={pageNumber}```, where ```currencyID``` should be either ```usd, aud, jpy```, and ```paginationLimit``` should be from 1 to 10 and has a default value of 10, and ```pageNumber``` can be any number and has a default value of 1

##### Examples
- http://localhost:8080/coins/bitcoin
- http://localhost:8080/coins/ethereum
- http://localhost:8080/coins/markets?vs_currency=usd
- http://localhost:8080/coins/markets?vs_currency=usd&per_page=5&page=2
- http://localhost:8080/coins/markets?vs_currency=jpy&per_page=3&page=3

### API Testing
##### Instructions
1. Make sure you are in the root directory and the current branch is ```api-tests```
2. Run ```cd api-backend``` and then ```mvn clean install``` to build the target ```jar``` file
3. Return to the root directory and then run ```docker-compose up --build -d``` to build the images and run the API on ```http://localhost:8080``` and access the Wiremock server on ```http://localhost:9999```
4. Run ```cd api-testing``` and ```mvn clean test``` to run the automated REST Assured API tests
5. Stop the API by returning to the root directory and running ```docker-compose down```
