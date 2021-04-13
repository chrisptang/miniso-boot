package com.miniso.boot.autoconfiguration.dubbo;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.miniso.boot.autoconfiguration.common.EnvUtil;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.miniso.boot.autoconfiguration.NetworkUtil.isServerUp;

/**
 * 如果配置了多个dubbo注册地址，比如使用的是zookeeper集群，则随机选择一个能够连通的地址；
 */
public class RegistrySelector {

    private static final String ADDRESS_DELIMITER = ",";

    /**
     * dubbo支持配置多个zookeeper地址，如：zookeeper://server1:2181,zookeeper://server2:2181
     * 此方法通过socket筛选出第一个能够ping通的地址作为首要的地址；
     *
     * @param originalAddress
     * @return
     */
    public static String selectPremierRegistryAddress(String originalAddress, String appId) {
        if (StringUtils.isEmpty(originalAddress)) {
            throw new IllegalArgumentException("Address should not be empty");
        }
        if (!originalAddress.contains(ADDRESS_DELIMITER)) {
            //如果只有一个地址，则直接返回；
            return originalAddress;
        }

        final String[] addressList = originalAddress.split(ADDRESS_DELIMITER);

        String localhost = System.getProperty("host.ip", "");
        if (StringUtils.isEmpty(localhost)) {
            localhost = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
        }

        int hash = Math.abs((localhost + appId + EnvUtil.getEnv()).hashCode());
        for (int index = hash % addressList.length, runs = 0; runs < addressList.length; runs++, index++) {
            String address = addressList[index % addressList.length];
            if (isServerUp(address)) {
                LinkedHashSet<String> linkedHashSet = new LinkedHashSet();
                linkedHashSet.add(address);

                //将剩余的address放入最后；
                for (String restAddress : addressList) {
                    if (!address.equals(restAddress)) {
                        linkedHashSet.add(restAddress);
                    }
                }

                return linkedHashSet.stream().collect(Collectors.joining(ADDRESS_DELIMITER));
            }
        }

        throw new RuntimeException("None of these registry is up:" + originalAddress);
    }

    public static String selectPremierRegistryAddress(String originalAddress) {
        //使用JVM参数app.id来表示application name；
        //一般来说都是spring.application.name
        String appId = System.getenv("app.id");
        if (StringUtils.isEmpty(appId)) {
            appId = System.getProperty("app.id", "___ops-no-app.id-found___");
        }
        return selectPremierRegistryAddress(originalAddress, appId);
    }
}
