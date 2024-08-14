#!/bin/bash
# Docker와 Docker Compose 설치 (이미 설치되어 있다면 이 부분은 생략 가능)
sudo apt-get update
sudo apt-get install -y docker.io docker-compose

# .env 파일이 이미 존재하는지 확인
if [ ! -f /home/ubuntu/app/.env ]; then
    echo "Warning: .env file not found. Make sure it exists and contains necessary environment variables."
fi

# 필요한 경우 .env 파일의 권한 설정
sudo chown ubuntu:ubuntu /home/ubuntu/app/.env
sudo chmod 600 /home/ubuntu/app/.env