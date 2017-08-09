package org.pub.DataPartitoin.util;

import java.util.HashMap;
import java.util.List;

/**
 * 基础参数类
 * Created by dongweizhao on 16/7/27.
 */
public class GnrParams {
    private HashMap<String, String> settings;
    private HashMap<String, String> tables;
    private List<String> interceptors;
    private String tablesStr;
    private String partitonKey;
    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    }

    public List<String> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<String> interceptors) {
        this.interceptors = interceptors;
    }

    public HashMap<String, String> getTables() {
        if (tables==null&&tablesStr!=null&&partitonKey!=null){
            tables=new HashMap<String,String>();
            String []tableArr=tablesStr.split(",");
            String []partitonKeyArr=partitonKey.split(",");
            for (int i=0;i<tableArr.length;i++){
                tables.put(tableArr[i].trim(),partitonKeyArr[i].trim());
            }
        }
        return tables;
    }

    public void setTables(HashMap<String, String> tables) {
        this.tables = tables;
    }


    public void setTablesStr(String tablesStr) {
        this.tablesStr = tablesStr;
    }


    public void setPartitonKey(String partitonKey) {
        this.partitonKey = partitonKey;
    }
}
