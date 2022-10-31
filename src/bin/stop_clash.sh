#! /bin/sh

pid=`ps -ef | grep clash | grep config | awk '{print $2}'`

if [ "x$pid" != "x" ]; then
  echo "kill clash ,pid : $pid"
	kill $pid
	notify-send  -i /home/feiya/clash/clashoff.png "clash 代理" "clash 已关闭"
else
	notify-send  -i /home/feiya/clash/clashoff.png "clash 代理" "clash 未开启"
fi
