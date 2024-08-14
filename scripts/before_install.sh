#!/bin/bash
# 필요한 디렉토리 생성 및 권한 설정
if [ ! -d /home/ubuntu/app ]; then
    mkdir -p /home/ubuntu/app
fi
chmod +x /home/ubuntu/app
# Java 설치 (이미 설치되어 있다면 이 부분은 생략 가능)
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk