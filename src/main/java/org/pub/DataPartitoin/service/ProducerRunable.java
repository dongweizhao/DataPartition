package org.pub.DataPartitoin.service;

import org.pub.DataPartitoin.util.PaginationUtil;
import org.pub.DataPartitoin.util.SysConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 生产者对应的多个队列
 * <p>
 * Created by dongweizhao on 17/6/23.
 */
public class ProducerRunable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerRunable.class);
    private List<ConcurrentLinkedQueue<String>> queueList;
    private int start, end, pageindex;
    private String tableName;
    private String partitionKey;
    private SysConfig sysConfig;

    public ProducerRunable(SysConfig sysConfig, List<ConcurrentLinkedQueue<String>> queueList, int start, int end, String tableName, String partitionKey) {
        this.queueList = queueList;
        this.start = start;
        this.end = end;
        this.tableName = tableName;
        this.partitionKey = partitionKey;
        this.sysConfig = sysConfig;
    }

    @Override
    public void run() {
        String separator = sysConfig.getGnrParams().getSettings().get("separator");
        int maxresult = Integer.valueOf(sysConfig.getGnrParams().getSettings().get("maxResults"));
        try {
            for (int i = start; i <= end; i++) {

                pageindex = i * maxresult;
                Connection connection=sysConfig.getDruidDataSource().getConnection();
                String nullValue=sysConfig.getGnrParams().getSettings().get("nullValue");
                PreparedStatement pre = connection.prepareStatement(PaginationUtil.getInstance().getPagingSql("select * from " + tableName, pageindex, maxresult));
                ResultSet rs = pre.executeQuery();
                int col = rs.getMetaData().getColumnCount();
                StringBuffer values;
                while (rs.next()) {
                    values = new StringBuffer();
                    for (int j = 1; j <= col; j++) {
                        String value = rs.getString(j);
                        if (value == null) {
                            value = nullValue;
                        }
                        values.append(value).append(separator);
                    }
                    values.append("\n");
                    queueList.get(sysConfig.getRuleAlgorithm().calculate(rs.getString(partitionKey))).offer(values.toString());
                }
                connection.close();
            }
        } catch (Exception e) {
            LOG.error("{}线程,表名:{},生产数据失败", Thread.currentThread().getName(), tableName,e);
        } finally {
            LOG.debug("{}线程,表名:{},生产数据完成", Thread.currentThread().getName(),tableName);
        }
    }
}
