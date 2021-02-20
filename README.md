# ssm-data-rest-ssm-sample and oauth2 with Azure AD

## 🧨 environmental variables specifications
```bash
brew install direnv
direnv allow 
```
create .envrc file then set env variables

## 🏯 Build Run
```bash
java --version
./mvnw spring-boot:run
```

## 🎇 Run with docker
```bash
./mvnw spring-boot:build-image
docker run -p 8080:8080 -d data-rest-stastemachine:0.0.1-SNAPSHOT
```
