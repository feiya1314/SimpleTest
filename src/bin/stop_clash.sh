#! /bin/sh

pid=`ps -ef | grep clash | grep config | awk '{print $2}'`

if [ "x$pid" != "x" ]; then
  echo "kill clash ,pid : $pid"
	kill $pid
fi
