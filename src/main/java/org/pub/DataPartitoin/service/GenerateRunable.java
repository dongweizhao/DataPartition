package org.pub.DataPartitoin.service;

import org.pub.DataPartitoin.util.PaginationUtil;
import org.pub.DataPartitoin.util.SysConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 数据分配切分核心线程类
 * Created by dongweizhao on 17/7/14.
 */
public class GenerateRunable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(GenerateRunable.class);
    private String table;
    private String partitionKey;
    private SysConfig sysConfig;
    public GenerateRunable(SysConfig sysConfig,String table, String partitionKey) {
        this.sysConfig=sysConfig;
        this.table = table;
        this.partitionKey = partitionKey;
    }

    @Override
    public void run() {
        try {
            List<ConcurrentLinkedQueue<String>> queueList = new ArrayList<ConcurrentLinkedQueue<String>>();
            int count = Integer.valueOf(sysConfig.getGnrParams().getSettings().get("dataCount")), sysTheadCount = Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < count; i++) {
                queueList.add(new ConcurrentLinkedQueue<String>());
            }
            //生产者线程数
            int proCount = sysTheadCount;
            //最小数据切分页数
            int partitionPage = 10;
            //数据总页数
            int page = getPage();
            ExecutorService producerService = Executors.newFixedThreadPool(proCount);
            if (page <= partitionPage) {
                producerService.execute(new ProducerRunable(sysConfig,queueList, 0, page, table, partitionKey));
            } else {
                //每个线程平均分配的页数
                int avgPagetoThread = page / proCount;
                //分配余数页数
                int modPage = page % proCount;
                for (int j = 0; j < page; j += avgPagetoThread) {
                    producerService.execute(new ProducerRunable(sysConfig,queueList, j, (page - j < avgPagetoThread ?j - 1 + modPage : j + avgPagetoThread - 1), table, partitionKey));
                }

            }
            //消费者分配
            int conCount = count;
            if (conCount > sysTheadCount) {
                int mod = conCount % sysTheadCount;
                if (mod == 0)
                    conCount = sysTheadCount;
                else
                    conCount = sysTheadCount + mod;
            }
            //平均负责的桶
            int avgNode = count / conCount;
            ExecutorService consumerService = Executors.newFixedThreadPool(conCount);
            for (int i = 0; i < count; i += avgNode) {
                consumerService.execute(new ConsumerRunable(sysConfig,queueList, i, i+avgNode - 1, count,table,producerService));
            }

            producerService.shutdown();
            consumerService.shutdown();

            while (true) {
                if (consumerService.isTerminated()) {
                    break;
                }
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (Exception e) {
            LOG.error("{}线程,表名:{},数据切分异常", Thread.currentThread().getName(), table, e);
        } finally {
            LOG.info("{}线程,表名:{},数据切分完成", Thread.currentThread().getName(), table);
        }
    }

    /**
     * 计算总页数
     *
     * @return
     */
    private int getPage() {
        int maxResult = Integer.valueOf(sysConfig.getGnrParams().getSettings().get("maxResults"));
        try {
            int count = queryDataCount();
            BigDecimal page1 = new BigDecimal(count * 1.0
                    / (maxResult));
            return Integer.parseInt(page1.setScale(0, BigDecimal.ROUND_UP).toString());
        } catch (Exception e) {
            LOG.error("{}线程,表名:{},计算数据总页数异常", Thread.currentThread().getName(), table, e);
        }
        return 0;
    }

    /**
     * 查询数据总条数
     *
     * @return
     */
    private int queryDataCount() {
        try {
            Connection connection=sysConfig.getDruidDataSource().getConnection();
            PreparedStatement ps = connection.prepareStatement(PaginationUtil.getInstance().getCountSql(table));
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                return rs.getInt(1);
            }
           connection.close();
        } catch (SQLException e) {
            LOG.error("{}线程,表名:{},查询数据总条数异常", Thread.currentThread().getName(), table, e);
        }
        return 0;
    }
}
