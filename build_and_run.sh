#!/bin/sh
#The following need to be run on startup (default)
set -e
docker image prune

pushd messaging-utilities-3.2
chmod u+x build.sh
./build.sh
popd

pushd facade
 bash build.sh
popd

pushd account
bash build.sh
popd

pushd token
bash build.sh
popd

pushd payment
bash build.sh
popd

#deploying the docker-containers (services) in the background specified in the docker-compose.yml file
docker-compose build
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account facade payment token