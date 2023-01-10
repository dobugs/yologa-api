#!/bin/bash

source ~/.bash_profile

APP_NAME=yologa-api
JAR_NAME=yologa-api-0.0.1-SNAPSHOT.jar

# 현재 실행중인 서버가 있으면 잡아서 종료
CURRENT_PID=$(pgrep -f $APP_NAME)
if [ -z $CURRENT_PID ]
then
  echo ">>>> java process not found."
else
  echo ">>>> PID: $CURRENT_PID kill."
  kill -15 $CURRENT_PID
  sleep 45
fi

# .jar 파일 java 실행
echo "build project"
cd ~
cd $APP_NAME
./gradlew bootJar

# kill 8080 port
fuser -k -n tcp 8080

echo ">>>> $APP_NAME execute."
cd build/libs
java -jar $JAR_NAME &

sleep 20
CURRENT_PID=$(pgrep -f $APP_NAME)
echo ">>>> New PID: $CURRENT_PID"
