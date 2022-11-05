#! /bin/sh

enable=$(gsettings get com.deepin.dde.touchpad touchpad-enabled)

if [ $enable = true ];then
        gsettings set com.deepin.dde.touchpad touchpad-enabled false
        notify-send  "触控板" "关闭触控板"
fi

if [ $enable = false ];then
        gsettings set com.deepin.dde.touchpad touchpad-enabled true
        notify-send  "触控板" "开启触控板"
fi