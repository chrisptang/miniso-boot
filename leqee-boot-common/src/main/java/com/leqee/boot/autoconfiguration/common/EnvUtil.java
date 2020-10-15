package com.leqee.boot.autoconfiguration.common;

public class EnvUtil {

    private static final String DEFAULT_ENV = "dev";

    private static volatile String currentEnv;

    public static String getEnv() {
        if (currentEnv != null && currentEnv.trim().length() > 0) {
            return currentEnv;
        }
        String env = System.getenv("env");//系统的env属性为最高优先级；
        if (env == null || env.trim().length() <= 0) {
            //通过leqee-boot.env覆盖，可以在java启动参数添加 -Dleqee-boot.env=dev/local/staging/test/prod来指定；
            env = System.getProperty("leqee-boot.env", env);
        }
        if (env == null || env.trim().length() <= 0) {
            env = DEFAULT_ENV;//默认
        }
        if (!System.getProperties().containsKey("env")) {
            System.setProperty("env", env);
        }

        return currentEnv = env;
    }
}
