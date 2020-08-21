FROM java:8-jdk-alpine

COPY ./target/CryptoAPI-0.0.1-SNAPSHOT.jar /usr/app/

WORKDIR /usr/app

RUN sh -c 'touch CryptoAPI-0.0.1-SNAPSHOT.jar'

ENTRYPOINT ["java","-jar","CryptoAPI-0.0.1-SNAPSHOT.jar"]