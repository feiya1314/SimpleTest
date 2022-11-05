#! /bin/sh

check_status(){
  status=`dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.GetProxyMethod | grep manual`	
	#echo $status
	if [ "x$status" != "x" ]; then
    echo "系统代理当前状态: 开启"
    return 1
  else
    echo "系统代理当前状态: 关闭"
    return 2
  fi
}

proxy_on(){
#	export no_proxy=localhost,127.0.0.0/8,::1
#	export https_proxy=http://127.0.0.1:7890
#	export http_proxy=http://127.0.0.1:7890
#	export SOCKS_SERVER=socks5://127.0.0.1:7891
	#dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.SetProxy string:'http' string:'127.0.0.1' string:'7890'
	#dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.SetProxy string:'https' string:'127.0.0.1' string:'7890'
	#dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.SetProxy string:'socks' string:'127.0.0.1' string:'7891'	
	check_status
	status=$?
	if [ $status = 1 ]; then
		#echo -e "已开启代理"
		notify-send  -i /home/feiya/clash/proxyon_64.png "系统代理" "系统代理开启中"
		return 0
  fi
  # 也可以使用 gsettings set org.gnome.system.proxy mode 'manual'
	dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.SetProxyMethod string:'manual' > /dev/null
	echo "已开启代理"
}

proxy_off(){
  #unset no_proxy
  #unset https_proxy
  #unset http_proxy
  #unset SOCKS_SERVER
  check_status
  status=$?
  if [ $status = 2 ]; then
    #echo -e "已关闭代理"
		notify-send  -i /home/feiya/clash/proxyoff_64.png "系统代理" "系统代理未开启"
    return 0
  fi
	dbus-send --session --print-reply --dest=com.deepin.daemon.Network /com/deepin/daemon/Network com.deepin.daemon.Network.SetProxyMethod string:'none' > /dev/null
  echo "已关闭代理"
}

param=$1

#echo "input $param"

if [ "$param" = "on" ]; then
	proxy_on
fi

if [ "$param" = "off" ]; then
   proxy_off 
fi

if [ "$param" = "status" ]; then
	check_status
fi
