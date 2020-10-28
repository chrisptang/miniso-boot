package com.leqee.boot.autoconfiguration;

import com.dianping.cat.Cat;
import com.dianping.cat.configuration.NetworkInterfaceManager;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

public class NetworkUtil {
    /**
     * 判断一个给定的URL是否能够连通；通过Socket ping实现；
     *
     * @param address e.g：http://some-host:3000, or, dubbo://127.0.0.2:2281
     * @return
     */
    public static boolean isServerUp(String address) {
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

    /**
     * 同isServerUp(String address)
     *
     * @param host hostname or ip
     * @param port socket port number
     * @return
     */
    public static boolean isServerUp(String host, int port) {
        return isServerUp(String.format("custom://%s:%d", host, port));
    }

    /**
     * 探测本地某一端口号是否已经被占用
     *
     * @param port socket port number
     * @return
     */
    public static boolean isLocalPortUsed(int port) {
        String localhost = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
        return isServerUp(localhost, port);
    }
}
