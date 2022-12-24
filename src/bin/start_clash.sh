#! /bin/sh
pid=`ps -ef | grep clash | grep config.yml | awk '{print $2}'`

clash_home=~/apps/clash

if [ "x$pid" != "x" ]; then
  echo "clash running,pid : $pid"
	notify-send  -i $clash_home/clashon.png "clash 代理" "clash 正在运行中"
  exit 0
fi

notify-send  -i $clash_home/clashon.png "clash 代理" "开启 clash"
cd $clash_home

nohup ./clash -f config.yml 2>&1 > ./clash.log &

exit
