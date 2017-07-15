package org.pub.DataPartitoin.util;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 工具类
 * Created by dongweizhao on 16/7/27.
 */
public class DMUtils {
    static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();

    public static SimpleDateFormat getSdf() {
        SimpleDateFormat simpleDateFormat = threadLocal.get();
        if (simpleDateFormat == null) {
            threadLocal.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"));
        }
        return threadLocal.get();
    }

    private static ClassPathXmlApplicationContext classPathXmlApplicationContext;



    public static void setClassPathXmlApplicationContext(ClassPathXmlApplicationContext cxn) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        classPathXmlApplicationContext = cxn;
    }

    public static Object getBeanById(String id){
        if (id == null){
            return null;
        }
        return classPathXmlApplicationContext.getBean(id);
    }

    private static GnrParams gnrParams;

    public static GnrParams getGnrParams() {
        if (gnrParams == null) {
            gnrParams = (GnrParams) classPathXmlApplicationContext.getBean("gnrParams");
        }
        return gnrParams;
    }






    /**
     * 返回自定义格式的当前日期时间字符串
     * <p/>
     * 格式规则
     *
     * @return String 返回当前字符串型日期时间
     */
    public static String conventDatetoStr(Date date) {
        String returnStr = null;
        returnStr = getSdf().format(date);
        return returnStr;
    }

    /**
     * <p/>
     * 格式规则
     *
     * @return String 返回当前字符串型日期时间
     */
    public static String getCurrentDatetoStr() {
        String returnStr = null;
        returnStr = getSdf().format(new Date());
        return returnStr;
    }

    public static String getUserId(long i) {
        return "UR" + leftPad("0", 12, i);
    }

    public static String leftPad(String fix, int len, long val) {
        return String.format("%" + fix + len + "d", new Object[]{val});
    }

    public static String getSequence(String i, int k) {
        return String.format("%16s%04d", new Object[]{i, k});
    }

    public static int getPage(int count, int maxResult) {
        BigDecimal page1 = new BigDecimal(count * 1.0
                / maxResult);
        return Integer.parseInt(page1.setScale(0, BigDecimal.ROUND_UP).toString());
    }

    public static FileOutputStream getFileOutputStream(String path, String dir, String fileName) throws IOException {
        File file = FileHelper.mkdir(path + dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        return new FileOutputStream(file);

    }


    /**
     * toMD5 <br/>
     * MD5加密<br/>
     *
     * @param plainText void
     * @throws
     * @since 1.0.0
     */
    public static String toMD5(String plainText) {

        StringBuffer buf = new StringBuffer("");
        try {
            // 生成实现指定摘要算法的MessageDigest对象。
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 使用指定的字节数组更新摘要。
            md.update(plainText.getBytes());
            // 通过指定诸如填充之类的最终操作完成哈希计算。
            byte b[] = md.digest();
            // 生成具体的md5密码到buf数组
            int i;
            for (int offset = 0; offset < b.length; ++offset) {

                i = b[offset];
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buf.toString();
    }



    public static Object getValues(Object obj, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return obj.getClass().getMethod(name).invoke(obj);
    }

    public static void setValue(Object obj, Object value, Class cls, String setter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        cls.getMethod(setter, value.getClass()).invoke(obj, value);
    }



    /**
     * 判断数据是否为空
     *
     * @param obj
     * @return
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null)
            return true;
        if (obj == "")
            return true;
        if (obj instanceof String) {
            if (((String) obj).length() == 0) {
                return true;
            }
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) {
                return true;
            }
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0)
                return true;
        }
        return false;
    }

    /**
     * 执行shell
     *
     * @param shell shell脚本名称
     * @param arg 输入的参数
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    public static boolean runShell(String shell, String... arg) throws InterruptedException, IOException {
        String execPara="";
        for(String s:arg){
            execPara+=" "+s;
        }
        Process process = Runtime.getRuntime().exec("sh " + shell + execPara);
        int c = process.waitFor();
        if (c != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     *
     * @param l 需转化的数据po的list
     * @param keyArg 作为key的数据的key
     * @param valueArgs 作为value的数据的key
     * @return 字典map
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Map objList2dic(List<?> l, String keyArg, String... valueArgs) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String keyFunc = "get" + keyArg.substring(0, 1).toUpperCase() + keyArg.substring(1);
        Map<String, Map> map = new HashMap<>();
        for (Object o : l) {
            String keyVal = (String) getValues(o, keyFunc);
            if (isEmpty(map.get(keyVal))) {
                Map<String, String> valueMap = new HashMap<>();
                for (String s : valueArgs) {
                    String valFunc = "get" + s.substring(0, 1).toUpperCase() + s.substring(1);
                    valueMap.put(s, (String) getValues(o, valFunc));
                }
                map.put(keyVal, valueMap);
            } else {
                throw new RuntimeException("the value that get from list must be unique,the duplicate key:" + keyVal);
            }
        }
        return map;
    }


}
