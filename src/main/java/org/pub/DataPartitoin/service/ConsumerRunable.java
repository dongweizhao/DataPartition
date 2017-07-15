package org.pub.DataPartitoin.service;

import org.pub.DataPartitoin.util.DMUtils;
import org.pub.DataPartitoin.util.SysConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * 消费者
 * 每个消费者最小负责一个桶
 * Created by dongweizhao on 17/6/23.
 */
public class ConsumerRunable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ConsumerRunable.class);
    private List<ConcurrentLinkedQueue<String>> queueList;
    private int sc, ec;
    private ExecutorService executorService;
    private FileOutputStream[] fop = null;
    private String table;
    private SysConfig sysConfig;
    public ConsumerRunable(SysConfig sysConfig, List<ConcurrentLinkedQueue<String>> queueList, int sc, int ec, int count, String table, ExecutorService executorService) {
        this.queueList = queueList;
        this.sc = sc;
        this.ec = ec;
        this.executorService = executorService;
        fop = new FileOutputStream[count];
        this.table=table;
        this.sysConfig=sysConfig;
    }


    @Override
    public void run() {
        Queue<String> queue;
        String path=sysConfig.getGnrParams().getSettings().get("path");
        try {
            int i = sc;
            while (true) {

                queue = queueList.get(i);

                //判断生产者是否结束,并且队列无值,则退出。
                if (executorService.isTerminated()) {
                    int flag = sc, j = sc;
                    for (; j <= ec; j++) {
                        if (queueList.get(j).isEmpty()) flag++;
                        else flag--;
                    }
                    if (flag == j) {
                        return;
                    }
                }

                if (fop[i] == null) {
                    fop[i] = DMUtils.getFileOutputStream(path,table,table+"_"+i+".txt");
                }
                for (;;){
                    if (!queue.isEmpty()) {
                        fop[i].write(queue.poll().getBytes());
                    }else {
                        break;
                    }
                }
                if (++i > ec) {
                    i = sc;
                }
            }
        } catch (Exception e) {
            LOG.error("生成文件线程{},表名:{},生成文件失败", Thread.currentThread().getName(),table, e);
        } finally {
            for (int i = sc; i <= ec; i++) {
                try {
                    fop[i].close();
                } catch (IOException e) {
                    LOG.error("生成文件线程{},表名:{},关闭文件流失败", Thread.currentThread().getName(), table,e);
                }
            }
            LOG.debug(",{}线程,表名:{},消费数据完成", Thread.currentThread().getName(),table);
        }

    }


}
