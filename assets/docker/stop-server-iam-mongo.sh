#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.server-iam-mongo.yml down --volumes
