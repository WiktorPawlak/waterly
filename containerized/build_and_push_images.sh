#!/bin/bash -e

if [ $# -lt 2 ]; then
    echo "Provide docker hub key and tag"
    exit 1
fi

REGISTRY="docker.io/matino1"
SECRET=$1
TAG=$2

if ! docker login --username "matino1" --password "${SECRET}"; then
    echo "Wrong docker hub secret key"
    exit 1
fi

echo "Building app image ..."
docker build -f ./app/Dockerfile -t ${REGISTRY}/ssbd06:app-${TAG} ../
docker push ${REGISTRY}/ssbd06:app-${TAG}
echo "App image pushed with tag: app-${TAG}"

echo "Building db image ..."
docker build -t ${REGISTRY}/ssbd06:db-${TAG} ./db
docker push ${REGISTRY}/ssbd06:db-${TAG}
echo "Db image pushed with tag: db-${TAG}"