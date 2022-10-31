#! /bin/sh

clash_status(){
	pid=`ps -ef | grep clash | grep config | awk '{print $2}'`

	if [ "x$pid" != "x" ]; then
  	echo "clash running,pid : $pid"
  	notify-send  -i /home/feiya/clash/clashon.png "clash 代理" "clash 正在运行中"
	else
		notify-send  -i /home/feiya/clash/clashoff.png "clash 代理" "clash 未开启"
	fi
}

clash_on(){
	sh /home/feiya/clash/start_clash.sh
}

clash_off(){
	sh /home/feiya/clash/stop_clash.sh
}

param=$1

if [ "$param" = "on" ]; then
	clash_on
fi

if [ "$param" = "off" ]; then
   clash_off 
fi

if [ "$param" = "status" ]; then
	clash_status
fi
