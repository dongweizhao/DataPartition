package org.pub.DataPartitoin.util;

import com.alibaba.druid.pool.DruidDataSource;
import io.mycat.config.model.rule.RuleAlgorithm;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * Created by dongweizhao on 17/7/15.
 */
@Service("sysConfig")
public class SysConfig {
    @Resource
    private GnrParams gnrParams;
    @Resource
    private DruidDataSource druidDataSource;
    @Resource
    private RuleAlgorithm ruleAlgorithm;

    public GnrParams getGnrParams() {
        return gnrParams;
    }

    public DruidDataSource getDruidDataSource() {
        return druidDataSource;
    }

    public RuleAlgorithm getRuleAlgorithm() {
        return ruleAlgorithm;
    }

}
