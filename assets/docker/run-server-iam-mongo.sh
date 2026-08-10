#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.server-iam-mongo.yml pull
#docker-compose -f "$(pwd)"/docker-compose.server-iam-mongo.yml up -d
docker-compose -f "$(pwd)"/docker-compose.server-iam-mongo.yml up
