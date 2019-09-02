#!/usr/bin/env bash
CONTAINER_NAME=spring-boot-demo-docker
PORT=9000

docker build -t ${CONTAINER_NAME} .

if [[ $(docker ps -aq --filter name=${CONTAINER_NAME}) ]]; then
    docker rm -f ${CONTAINER_NAME}
fi

docker run -d --name ${CONTAINER_NAME} \
           -p ${PORT}:8080 \
           -t spring-boot-demo-docker