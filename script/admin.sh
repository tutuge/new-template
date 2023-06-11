#!/bin/sh

#进程名称
process_name=ruoyi-admin.jar

while [ 0 -eq 0 ]
do
    ps -ef|grep $process_name |grep -v grep
    # $? -ne 0 不存在，$? -eq 0 存在
    if [ $? -ne 0 ]
    then
    echo ">>>telegram is stop,to start"
    #启动进程
    nohup java -Dspring.profiles.active=prod -Dthread-pool.enabled=true -Dserver.port=8080  -jar    -Xms512m -Xmx512m /home/jar/ruoyi-admin.jar >/dev/null 2>&1 &
    
    break
    else
    echo ">>>telegram is runing,to kill"
    #停止进程
    ps -ef | grep $process_name | grep -v grep | awk '{print $2}' | xargs kill -15
    #休眠一秒后判断
    sleep 1
    fi
done