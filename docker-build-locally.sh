#!/usr/bin/env bash

PREFIX="[DockerBuildLocally]"
GITHUB_ACTION_MAIN_WORKFLOW=".github/workflows/main.yml"
DEPLOYMENT_ENV_FILE=".github/deployment.env"

DOCKER_REGISTRY=$(grep "DOCKER_REGISTRY:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_IMAGE_SERVER_IAM=$(grep "DOCKER_IMAGE_SERVER_IAM:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_IMAGE_SERVER_RESOURCE_BURNER=$(grep "DOCKER_IMAGE_SERVER_RESOURCE_BURNER:" "$GITHUB_ACTION_MAIN_WORKFLOW" | awk '{print $2}' | tr -d "'")
DOCKER_VERSION=$(grep "^DOCKER_VERSION=" "$DEPLOYMENT_ENV_FILE" | cut -d= -f2)
DOCKER_IMAGE_IAM="$DOCKER_REGISTRY/$DOCKER_IMAGE_SERVER_IAM:$DOCKER_VERSION"
DOCKER_IMAGE_RESOURCE_BURNER="$DOCKER_REGISTRY/$DOCKER_IMAGE_SERVER_RESOURCE_BURNER:$DOCKER_VERSION"

echo "================================================================================================================="
echo "$PREFIX Maven build started"

./mvnw clean install -Dmaven.test.skip -DskipTests -T 4 || exit 1

echo "$PREFIX Maven build has been completed"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX Docker build started: $DOCKER_IMAGE_IAM"

docker build -t "$DOCKER_IMAGE_IAM" jbst-server-iam || exit 1

echo "$PREFIX Docker build has been completed: $DOCKER_IMAGE_IAM"
echo "================================================================================================================="

echo "================================================================================================================="
echo "$PREFIX Docker build started: $DOCKER_IMAGE_RESOURCE_BURNER"

docker build -t "$DOCKER_IMAGE_RESOURCE_BURNER" jbst-server-resource-burner || exit 1

echo "$PREFIX Docker build has been completed: $DOCKER_IMAGE_RESOURCE_BURNER"
echo "================================================================================================================="
