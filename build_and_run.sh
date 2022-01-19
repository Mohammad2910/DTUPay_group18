#!/bin/sh
#The following need to be run on startup (default)
set -e
docker image prune

cd messaging-utilities-3.2
chmod u+x build.sh
./build.sh

cd ..
cd facade
chmod u+x mvnw
./mvnw package

cd ..
cd account
mvn package

cd ..
cd token
mvn package

cd ..
cd payment
mvn package

cd ..
#deploying the docker-containers (services) in the background specified in the docker-compose.yml file
docker-compose build
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account facade payment token