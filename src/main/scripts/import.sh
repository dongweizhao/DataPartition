#!/bin/bash
################################数据节点导入脚本##############################
#参数1 代表操作类型 T或t代表目标库导入操作,DF或df代表clusterdb操作删除
#当参数1 为M或m时
###  参数2 为空表示导入所有表，参数2不为空，导入指索引表
#当参数1为T或t时
###  参数2代表数据节点 参数3代表表索引，具体索引参考TABLES数组
###  当参数2不为空，参数3为空，导入指定节点所有表
###  当参数2、参数3都不为空，导入指定节点指定表
###  当参数2、参数3都为空，导入所有节点所有表
###########################################################################
templog='${dyn.logback.home}/import_all.log';#日志目录
####导入文件记录、包括：生成目标文件，源数据文件
generatePath='${dyn.generate.path}';
date=`date "+%Y/%m/%d %T"`

plog="tee -a ${templog}";
if [ ! -e "$templog" ]; then
	#statements
	touch ${templog}
	echo "创建新文件：${templog}"|${plog}
else
	rm ${templog};
	touch ${templog};
	echo "删除老文件：${templog}，创建新文件：${templog}"|${plog}
fi

####表集合
TABLESTR=${dyn.tables}
TABLES=(${TABLESTR//,/ })
TAB_LEN=${#TABLES[*]}
echo "表数量：$TAB_LEN"

##导入列
  COLUMNS[0]="
  ID
 ,USER_ID
 ,GLOBAL_USER_ID
 ,LOGIN_NAME
 ,MOBILE_NO
 ,CERT_TYPE
 ,CERT_NO
 ,USER_NICK_NAME
 ,MOBILE_STAT
 ,EMAIL
 ,USER_LEVEL
 ,USER_CLASS_ID
 ,LOGIN_STATIC_PWD
 ,LOGIN_PWD_UPDATE_TIME
 ,CUST_PWD_UPDATE_TIME
 ,LOGIN_SIGN_PWD
 ,PWD_CHANGE_FLAG
 ,MSG_KEY
 ,SECURITY_AUTH_WAY
 ,SIGN_FLAG
 ,IS_DELETED
 ,GMT_CREATE
 ,GMT_MODIFIED
 ,RESERVER1
 ,RESERVER2
 ,RESERVER3
 ,ENCY_FACTOR
  ";


#目标库地址
${dyn.targert.node.datasource}
DBS_LEN=${#DBS[*]}
echo "数据库节点数量：$DBS_LEN"


###参数1 导入文件文件名称  参数2 表名称 参数3 导入列 参数4 数据节点 参数5 目标库与中间库标识 1目标库 2中间库
function packLoadScript(){
      COL=$3;TABLE=$2;
	  CODE="load data local infile '${generatePath}/$1.txt' replace into table $TABLE fields terminated by '${dyn.separator}' optionally enclosed by '' lines terminated by '\n'(
      ${COL}
     )${SETDEFAULT};
	  SHOW WARNINGS; 
      SHOW ERRORS; 
     SELECT count(*) as 导入条数 FROM ${TABLE}";
     echo "表:${TABLE}，导入数据节点:$4"|${plog}
     echo "数据节点:$4 , 执行命令:${CODE}"|${plog}
     echo "${CODE}"|$4|${plog}
}

######目标库导入 $1 数据节点索引 $2 导入表索引
function importTarget(){

    if [[ "$1" != ""  &&  "$2" == "" ]];then
         echo "遍历所有表，导入数据节点:${DBS[$1]}"|${plog}
        #最后两个table不导入目标库
        for (( i = 0; i < TAB_LEN; i++ )); do
          TABLE=${TABLES[i]}
          packLoadScript target/${TABLE}/${TABLE}"_"$1 ${TABLE} "${COLUMNS[i]}" "${DBS[$1]}" 1
        done
    
    elif [[ "$1" != ""  &&  "$2" != "" ]];then
            TABLE=${TABLES[$2]}
            packLoadScript target/${TABLE}/${TABLE}"_"$1 ${TABLE} "${COLUMNS[$2]}" "${DBS[$1]}" 1
    else
       echo "遍历所有表，导入所有数据节点"|${plog}
       
          for (( n = 0; n < DBS_LEN; n++ )); do
             NODE=${DBS[n]}
            for (( i = 0; i < TAB_LEN; i++ )); do
             TABLE=${TABLES[i]}
             COLUMN=${COLUMNS[i]}
             packLoadScript target/${TABLE}/${TABLE}"_"$n ${TABLE} "${COLUMNS[i]}" "$NODE" 1
            done
       done
    
    fi
}



if [[ "$1" != "" ]]; then
    if [[ "$1" == "T" || "$1" == "t" ]]; then
      echo "目标库导入流程开始"|${plog}
      importTarget $2 $3
      echo "目标库导入流程结束"|${plog}
    else 
      echo "参数输入错误，请重新输入"|${plog}
   fi
else
  echo "参数不能为空,请重新输入"|${plog}
fi


