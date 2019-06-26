#!/bin/bash  

main_jar_name=ibmp-route.jar

PID=$(ps -ef | grep $main_jar_name | grep -v grep | awk '{ print $2 }')  
if [$PID]; then
    kill $PID  
fi

nohup java -Xmx300m -Xms300m -Xmn200m -server -XX:-PrintGC -XX:-PrintGCDetails -XX:-PrintGCTimeStamps -Xloggc:logs/gc.log -jar $main_jar_name &

PID=0
PID=$(ps -ef | grep $main_jar_name | grep -v grep | awk '{ print $2 }')

   if [ $PID -ne 0 ]; then
      echo "(pid=$PID) [Start OK  $PID ]"
	  echo "ok" > startup_status.txt
   else
      echo "[Start Failed]"
	  echo "failed" > startup_status.txt
fi

tail -f nohup.out