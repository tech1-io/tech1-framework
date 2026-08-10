#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.server-iam-postgres.yml pull
#docker-compose -f "$(pwd)"/docker-compose.server-iam-postgres.yml up -d
docker-compose -f "$(pwd)"/docker-compose.server-iam-postgres.yml up
