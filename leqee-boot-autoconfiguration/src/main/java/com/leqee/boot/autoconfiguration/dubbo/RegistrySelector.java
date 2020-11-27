package com.leqee.boot.autoconfiguration.dubbo;

import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import static com.leqee.boot.autoconfiguration.NetworkUtil.isServerUp;

public class RegistrySelector {

    private static final String ADDRESS_DELIMITER = ",";

    /**
     * dubbo支持配置多个zookeeper地址，如：zookeeper://server1:2181,zookeeper://server2:21181
     * 此方法通过socket筛选出第一个能够ping通的地址作为首要的地址；
     *
     * @param originalAddress
     * @return
     */
    public static String selectPremierRegistryAddress(String originalAddress) {
        if (StringUtils.isEmpty(originalAddress)) {
            throw new IllegalArgumentException("Address should not be empty");
        }
        if (!originalAddress.contains(ADDRESS_DELIMITER)) {
            //如果只有一个地址，则直接返回；
            return originalAddress;
        }
        String[] addressList = originalAddress.split(ADDRESS_DELIMITER);

        for (String address : addressList) {
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


}
