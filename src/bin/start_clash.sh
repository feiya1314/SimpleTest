#! /bin/sh
pid=`ps -ef | grep clash | grep config | awk '{print $2}'`

if [ "x$pid" != "x" ]; then
  echo "clash running,pid : $pid"
  exit 0
fi


cd /home/feiya/clash

nohup ./clash -f config.yml 2>&1 > ./clash.log &
