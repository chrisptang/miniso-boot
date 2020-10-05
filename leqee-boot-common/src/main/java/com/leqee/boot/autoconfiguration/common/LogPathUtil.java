package com.leqee.boot.autoconfiguration.common;

import java.io.File;

public class LogPathUtil {


    /**
     * 确保日志目录存在；
     *
     * @param applicationName 应用名称，如ztc_web, ztc_data
     * @param module          组件模块，如cat, xxl-job, dubbo, apollo等
     */
    public static String ensureLogPath(String applicationName, String module) {
        //初始化CAT本地日志；
        String userHome = System.getProperty("user.home");
        if (userHome.endsWith("/")) {
            userHome = userHome.substring(0, userHome.length() - 1);
        }
        //初始化日志目录，如：/home/admin/logs/demo-app/cat
        String moduleLogHome = String.format("%s/logs/%s/%s", userHome, applicationName, module);
        File moduleLogHomePath = new File(moduleLogHome);
        if (!moduleLogHomePath.exists()) {
            moduleLogHomePath.mkdirs();
        } else if (!moduleLogHomePath.isDirectory()) {
            moduleLogHomePath.deleteOnExit();
            moduleLogHomePath.mkdirs();
        }

        return moduleLogHome;
    }
}
