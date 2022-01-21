#!/bin/bash
set -e
docker image prune -f

#deploying the docker-containers (services) in the background specified in the docker-compose.yml file
docker-compose build
docker-compose up -d rabbitMq
sleep 10
docker-compose up -d account facade payment token report