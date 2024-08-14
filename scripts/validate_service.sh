#!/bin/bash
# 애플리케이션이 실행 중인지 확인
sleep 10
if sudo systemctl is-active --quiet payment-api.service; then
    echo "Application is running"
    exit 0
else
    echo "Application is not running"
    exit 1
fi