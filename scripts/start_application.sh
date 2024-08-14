#!/bin/bash
# 애플리케이션 시작
cd /home/ubuntu/app
nohup java -jar payment-api-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &