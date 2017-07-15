//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.pub.DataPartitoin.util;

/**
 * 分页查询类
 */

public class PaginationUtil  {
    private  String dialect = "mysql";
    private GnrParams gnrParams;
    private static PaginationUtil paginationUtil=new PaginationUtil();
    public static PaginationUtil getInstance(){
        return paginationUtil;
    }

    public PaginationUtil() {
        gnrParams= (GnrParams) DMUtils.getBeanById("gnrParams");

        this.dialect=gnrParams.getSettings().get("dialect");
        if (this.dialect==null)this.dialect="mysql";
    }

    public  String getCountSql(String tableName) {
        return "select count(1) from "+tableName;
    }


    public   String getPagingSql(String querySelect, int offset, int pageSize) {
        StringBuffer pageSql = new StringBuffer();
        if ("oracle".equals(dialect)) {
            pageSql.append("select * from (select tmp_tb.*,ROWNUM row_id from (");
            pageSql.append(querySelect);
            pageSql.append(") tmp_tb where ROWNUM<=");
            pageSql.append(offset + pageSize);
            pageSql.append(") where row_id>");
            pageSql.append(offset);
        } else {
            if (!"mysql".equals(dialect)) {
                throw new RuntimeException("不支持的数据库类型" + dialect);
            }

            pageSql.append(querySelect);
            pageSql.append(" limit " + offset + "," + pageSize);
        }

        return pageSql.toString();
    }

}
