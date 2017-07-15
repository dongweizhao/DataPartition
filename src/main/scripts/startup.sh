. ./env.app.conf
. ./env.conf
. ./env.sh $1 $2
#$1:是否刷新$2:是否增量

####################################
###  $APP_NAME应用不可同时运行2个
####################################
#echo "[INFO]: 应用[$APP_NAME]正在初始化..";
if [ ! -d "$LOG_APP_HOME" ]; then
        mkdir -p "$LOG_APP_HOME"
        echo "[INFO]: 创建目录[$LOG_APP_HOME]成功."
fi
v_count=`ps -ef | grep "$RUN_COMMAND" | grep -v 'grep'|wc -l`
if [ $v_count -eq 0 ]; then
    echo "[INFO]: 应用[$APP_NAME]初始化完毕.";
else
		echo "[ERROR]: 已经有应用[$APP_NAME]在运行，无法再次启动!!";
		echo `ps -ef | grep "$RUN_COMMAND" | grep -v 'grep'`
		exit 0
fi

####################################
###  提交命令
####################################
echo "[INFO]: 应用[$APP_NAME]开始执行，时间: "`date +%Y%m%d_%H:%M:%S`
CONFIG_CLASSPATH=$CONFIG_HOME:$CONFIG_APP_HOME
if [ "$CONFIG_APP_COMMON_HOME" != "" ]; then
	CONFIG_CLASSPATH=$CONFIG_CLASSPATH:$CONFIG_APP_COMMON_HOME
fi
CLASS_PATH=$CONFIG_CLASSPATH:$APP_RUN_HOME/$CONFIG_NAME:$APP_RUN_HOME/$BIN_NAME:$(echo $APP_RUN_HOME/lib/*.jar|sed 's/ /:/g')
export CLASSPATH=$CLASS_PATH:$CLASSPATH
echo "CLASSPATH=$CLASSPATH"
echo "RUN_COMMAND=$RUN_COMMAND"
nohup $RUN_COMMAND >$LOG_APP_HOME/shell_${APP_NAME}_out.log 2>$LOG_APP_HOME/shell_${APP_NAME}_err.log &

sleep 2
#echo "[INFO]: 执行成功."
sleep 1

####################################
###  判断$APP_NAME是否启动成功
####################################
count=`ps -ef | grep "$RUN_COMMAND" | grep -v 'grep'|wc -l`
if [ $count -eq 0 ]; then
    echo "[INFO]: 应用[$APP_NAME]执行完成, !";
fi

echo "[INFO]: 查看输出日志目录[$LOG_APP_HOME/]"
