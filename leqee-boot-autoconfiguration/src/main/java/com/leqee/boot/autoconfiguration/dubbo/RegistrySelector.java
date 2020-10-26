package com.leqee.boot.autoconfiguration.dubbo;

import com.dianping.cat.Cat;
import org.springframework.util.StringUtils;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class RegistrySelector {

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
        if (!originalAddress.contains(".")) {
            //如果只有一个地址，则直接返回；
            return originalAddress;
        }
        String[] addressList = originalAddress.split(",");

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

                return linkedHashSet.stream().collect(Collectors.joining(","));
            }
        }

        throw new RuntimeException("None of these registry is up:" + originalAddress);
    }

    private static boolean isServerUp(String address) {
        try {
            URI url = new URI(address);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(url.getHost(), url.getPort()), 600);

            return true;
        } catch (Exception e) {
            Cat.logError("Unable to open socket toward:" + address, e);
        }

        return false;
    }
}
