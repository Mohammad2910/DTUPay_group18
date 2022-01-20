#!/bin/sh
#The following need to be run on startup (default)
set -e
docker image prune

pushd messaging-utilities-3.2
chmod u+x build.sh
./build.sh
popd

pushd facade
chmod u+x mvnw
./mvnw package
popd

pushd account
mvn package
popd

pushd token
mvn package
popd

pushd payment
mvn package
popd

#deploying the docker-containers (services) in the background specified in the docker-compose.yml file
docker-compose build
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account facade payment token