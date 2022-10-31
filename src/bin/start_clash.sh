#! /bin/sh
pid=`ps -ef | grep clash | grep config | awk '{print $2}'`

if [ "x$pid" != "x" ]; then
  echo "clash running,pid : $pid"
	notify-send  -i /home/feiya/clash/clashon.png "clash 代理" "clash 正在运行中"
  exit 0
fi

notify-send  -i /home/feiya/clash/clashon.png "clash 代理" "clash 开启"
cd /home/feiya/clash

nohup ./clash -f config.yml 2>&1 > ./clash.log &

exit
