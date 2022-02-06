#!/bin/bash

docker-compose down
docker system prune

docker-compose up -d --build --scale sa=3