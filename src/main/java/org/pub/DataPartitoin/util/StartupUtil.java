
package org.pub.DataPartitoin.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class StartupUtil {
    private static final Logger LOG = LoggerFactory.getLogger(StartupUtil.class);
    private static final String DEFAULT_SPRING_CONFIG = "classpath*:spring/**/*.xml";
    private ClassPathXmlApplicationContext ctx;
    private String[] configLocations;
    private String appname;
    private ServerSocket ss;
    private int port;

    public static StartupUtil startApplication(String appname, String[] args) {
        if(0!=args.length) {
            String t="";
            int argsLen=args.length;
            for (int i = 0; i < argsLen; ++i) {
                String k = i+1+"-"+args[i]+" ";
                t+=k;
            }
            LOG.info("启动参数:{}", t);
        }
        StartupUtil su = getInstance(appname, args);
        su.loadxml();
        //su.start();
        return su;
    }

    public static StartupUtil getInstance(String appname, String[] args) {
        StartupUtil su = null;
        if (null != args && args.length > 0) {
//            su = new StartupUtil(Integer.parseInt(args[0]));
            su = new StartupUtil();
        } else {
            su = new StartupUtil();
        }
        su.setAppname(appname);
        return su;

    }

    public StartupUtil() {
    }

    public StartupUtil(int port) {
        this.port = port;

        try {
            this.ss = new ServerSocket(port);
        } catch (IOException var3) {
            LOG.error(var3.getMessage(), var3);
        }

    }


    public void loadxml() {
        try {
            if (null == this.configLocations) {
                this.ctx = new ClassPathXmlApplicationContext(DEFAULT_SPRING_CONFIG);
            } else {
                this.ctx = new ClassPathXmlApplicationContext(this.configLocations);
            }

            this.ctx.start();
        } catch (Exception var2) {
            var2.printStackTrace();
            LOG.error("Application[{}] start failure!!", this.appname, var2);
            System.out.println("Application[" + this.appname + "] start failure !!");
            System.exit(-1);
        }

        LOG.info("Application[{}] start success!!", this.appname);
        System.out.println(">>[console]: ".concat("Application[{}] start success!!".replace("{}", this.appname)));
    }

    public void start() {
        try {
            if (0 == port) {
                System.in.read();
            } else {
                while (true) {
                    final Socket s = ss.accept();
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            BufferedReader input = null;
                            PrintWriter out = null;

                        }
                    }).start();
                }
            }
        } catch (IOException e) {
            LOG.error(appname, e);
        }
        LOG.info(appname);
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public ClassPathXmlApplicationContext getCtx() {
        return ctx;
    }
}