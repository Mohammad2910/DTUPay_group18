#!/bin/sh
#The following need to be run on startup (default)
set -e
docker image prune

#build facade
#cd facade
#chmod u+x mvnw
#./mvnw package
#cd ..

#cd payment_ms
#chmod u+x mvnw
#./mvnw package
#cd ..
cd DTU
cd messaging-utilities-3.2
chmod u+x build.sh
./build.sh

cd ..
cd Facade
chmod u+x mvnw
./mvnw package

#deploying the docker-containers (services) in the background specified in the docker-compose.yml file
docker-compose build
docker-compose up -d