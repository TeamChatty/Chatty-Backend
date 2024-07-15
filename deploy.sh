#!/usr/bin/env bash
IS_DEV1=$(docker ps | grep chatty1)
DEFAULT_CONF=" /etc/nginx/sites-available/dev.api.chattylab.org.conf"
MAX_RETRIES=20

check_service() {
  local RETRIES=0
  local URL=$1
  while [ $RETRIES -lt $MAX_RETRIES ]; do
    echo "Checking service at $URL... (attempt: $((RETRIES+1)))"
    sleep 3

    REQUEST=$(curl $URL)
    if [ -n "$REQUEST" ]; then
      echo "health check success"
      return 0
    fi

    RETRIES=$((RETRIES+1))
  done;

  echo "Failed to check service after $MAX_RETRIES attempts."
  return 1
}

if [ -z "$IS_DEV1" ];then
  echo "### DEV2 => DEV1 ###"

  echo "1. DEV1 이미지 받기"
  docker-compose pull chatty1

  echo "2. DEV1 컨테이너 실행"
  docker-compose up -d chatty1

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8080"; then
    echo "DEV1 health check 가 실패했습니다."
    exit 1
  fi

  echo "4. nginx 재실행"
  sudo cp /etc/nginx/sites-available/dev.api.chattylab.org1.conf /etc/nginx/sites-available/dev.api.chattylab.org.conf
  sudo nginx -s reload

  echo "5. DEV2 컨테이너 내리기"
  docker-compose stop chatty2
  docker-compose rm -f chatty2

else
  echo "### DEV1 => DEV2 ###"

  echo "1. DEV2 이미지 받기"
  docker-compose pull chatty2

  echo "2. DEV2 컨테이너 실행"
  docker-compose up -d chatty2

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8081"; then
      echo "DEV1 health check 가 실패했습니다."
      exit 1
    fi

  echo "4. nginx 재실행"
  sudo cp /etc/nginx/sites-available/dev.api.chattylab.org2.conf /etc/nginx/sites-available/dev.api.chattylab.org.conf
  sudo nginx -s reload

  echo "5. DEV1 컨테이너 내리기"
  docker-compose stop chatty1
  docker-compose rm -f chatty1
fi
#APP_NAME=chatty-api
#cd /home/ubuntu/chatty
#echo "> 실행 중인 Spring Boot 서비스 중지"
#docker compose down springBoot
#echo "> 도커 이미지 빌드"
#docker build -t "${APP_NAME}:latest" .
#echo "> 도커 컴포즈 실행"
#docker compose up -d
#echo "> 사용되지 않는 도커 이미지 삭제"
#docker image prune -f
#exit 0
