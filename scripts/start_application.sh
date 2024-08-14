#!/bin/bash
cd /home/ubuntu/app
docker-compose build
docker-compose up -d
docker image prune -f