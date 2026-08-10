#!/usr/bin/env bash

docker-compose -f "$(pwd)"/docker-compose.server-iam-postgres.yml down --volumes
