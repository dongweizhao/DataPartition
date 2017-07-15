package org.pub.DataPartitoin;


import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Before;
import org.junit.Test;
import org.pub.DataPartitoin.util.StartupUtil;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Unit test for simple App.
 */

public class AppTest {
    StartupUtil startupUtil = null;
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AppTest.class);

    @Before
    public void before() {
        startupUtil = StartupUtil.startApplication("DataMigration-Application", new String[]{});
    }

    @Test
    public void execute() throws SQLException {
        DruidDataSource druidDataSource=(DruidDataSource) startupUtil.getCtx().getBean("dataSource");
        String sql="select ID,USER_ID,GLOBAL_USER_ID,LOGIN_NAME,MOBILE_NO,CERT_TYPE,CERT_NO,USER_NICK_NAME,MOBILE_STAT,EMAIL,USER_LEVEL,USER_CLASS_ID,LOGIN_STATIC_PWD,LOGIN_PWD_UPDATE_TIME,CUST_PWD_UPDATE_TIME,LOGIN_SIGN_PWD,PWD_CHANGE_FLAG,MSG_KEY,SECURITY_AUTH_WAY,SIGN_FLAG,IS_DELETED,GMT_CREATE,GMT_MODIFIED,RESERVER1,RESERVER2,RESERVER3,ENCY_FACTOR from t_cap_user_auth_info";
        System.out.println("activeCount:"+druidDataSource.getActiveCount());
        Connection connection=druidDataSource.getConnection();
        PreparedStatement pre=connection.prepareStatement(sql);
        ResultSet rs=pre.executeQuery();
        while (rs.next()){
            System.out.println("rs1 = [" + rs.getString(1) + "], rs2 = [" + rs.getString("ID") + "]");
        }
        connection.close();
        System.out.println("activeCount:"+druidDataSource.getActiveCount());
    }
}
