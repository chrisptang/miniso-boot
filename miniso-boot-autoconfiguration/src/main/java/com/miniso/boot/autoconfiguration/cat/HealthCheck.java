package com.miniso.boot.autoconfiguration.cat;

import com.miniso.boot.client.result.Result;

/**
 * 用来检测spring应用的健康状况，并上报CAT，用以监控；
 */
public interface HealthCheck {


    /**
     * 可以添加任何监控检查的逻辑；
     * 比如可以添加dubbo接口的校验、页面的检验等等；
     *
     * @return 是否成功；
     */
    Result<Boolean> check();

    String getName();
}
