#! /bin/sh

pid=`ps -ef | grep clash | grep config.yml | awk '{print $2}'`

clash_home=~/apps/clash

if [ "x$pid" != "x" ]; then
  echo "kill clash ,pid : $pid"
    kill $pid
    notify-send  -i $clash_home/clashoff.png "clash 代理" "clash 已关闭"
else
    notify-send  -i $clash_home/clashoff.png "clash 代理" "clash 未开启"
fi