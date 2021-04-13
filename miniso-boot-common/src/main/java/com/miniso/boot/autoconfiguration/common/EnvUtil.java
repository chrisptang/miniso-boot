package com.miniso.boot.autoconfiguration.common;

public class EnvUtil {

    private static final String DEFAULT_ENV = SupportedEnv.Dev.getName();

    private static volatile String currentEnv;

    /**
     * 获取当前应用的所属环境：local/dev/fat/prod，分别对应：本地环境/开发环境/集成测试环境/生产环境
     * <p>
     * 获取env参数的优先级（从高到低）：
     * 1、JVM启动参数，-Denv=local/dev/fat/prod；
     * 2、Linux系统环境变量，ENV；
     * 3、JVM 启动参数 -Dleqee-boot.env=local/dev/fat/prod；
     * 4、默认：dev；
     *
     * @return
     */
    public static String getEnv() {
        if (currentEnv != null && currentEnv.trim().length() > 0) {
            return currentEnv;
        }
        String env = System.getProperty("env");//JVM的env启动参数为最高优先级；

        if (env == null || env.trim().length() <= 0) {
            //通过Linux环境变量 ENV来指定；
            env = System.getenv("ENV");
        }

        if (env == null || env.trim().length() <= 0) {
            //通过leqee-boot.env覆盖，可以在java启动参数添加 -Dleqee-boot.env=dev/local/staging/test/prod来指定；
            env = System.getProperty("miniso-boot.env", env);
        }
        if (env == null || env.trim().length() <= 0) {
            env = DEFAULT_ENV;//默认
        }
        if (!System.getProperties().containsKey("env")) {
            System.setProperty("env", env);
        }

        return currentEnv = env.toLowerCase();
    }

    public static SupportedEnv getSupportedEnv() {
        String env = getEnv();
        return SupportedEnv.getByName(env);
    }
}
