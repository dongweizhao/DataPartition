package org.pub.DataPartitoin.run;

import org.pub.DataPartitoin.service.GenerateRunable;
import org.pub.DataPartitoin.util.DMUtils;
import org.pub.DataPartitoin.util.StartupUtil;
import org.pub.DataPartitoin.util.SysConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *主入口
 */
public class Startup {
    private static final Logger LOG = LoggerFactory.getLogger(Startup.class);


    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            LOG.info("数据切分开始,时间{}", sdf.format(new Date()));
            StartupUtil startupUtil = StartupUtil.startApplication("DataPartition-Application", args);
            DMUtils.setClassPathXmlApplicationContext(startupUtil.getCtx());
            SysConfig sysConfig= (SysConfig) DMUtils.getBeanById("sysConfig");
            ExecutorService tableExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            for (Map.Entry<String, String> entry : sysConfig.getGnrParams().getTables().entrySet()) {
                tableExecutor.execute(new GenerateRunable(sysConfig,entry.getKey(), entry.getValue()));
            }
            tableExecutor.shutdown();
            //检查执行作业是否完成
            while (true) {
                if (tableExecutor.isTerminated()) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }
            SimpleDateFormat edf = new SimpleDateFormat("HH:mm:ss");
            LOG.info("数据切分完成，时间{}", edf.format(new Date()));
        } catch (Exception e) {
            LOG.error("数据切分异常",e);
        }
    }
}
