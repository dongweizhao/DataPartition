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
        return tables;
    }

    public void setTables(HashMap<String, String> tables) {
        this.tables = tables;
    }
}
