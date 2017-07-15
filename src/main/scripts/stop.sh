. ./env.app.conf
. ./env.conf
. ./env.sh
 
pids=`ps -ef|grep "$RUN_COMMAND" | grep -v "grep"|awk '{print $2}'`
if [ "$pids" = "" ]; then
     echo "[INFO]: Application[$APP_NAME] does not started !!"
else
	 for pid in ${pids}; do
	 	kill -9 $pid 1>/dev/null 2>&1
 		echo "[INFO]: Application[$APP_NAME](pid=$pid) has stopped !!"
	 done
fi