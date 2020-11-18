package com.leqee.boot.autoconfiguration;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;

@Slf4j
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
            log.warn("Unable to open socket toward:" + address);
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


    /**
     * 为@param module选一个可用的端口号；
     *
     * @param initialPort 初始端口号
     * @param module      模块名称，用作日志
     * @return
     */
    public static int pickAvailablePort(int initialPort, String module) {
        for (int i = 0; i < 20; i++) {
            if (NetworkUtil.isLocalPortUsed(initialPort)) {
                log.warn(String.format("*******\n*******\n*******\n%s port has been used:%d", module, initialPort));
                log.warn(String.format("Will try to use new port:%d for module:%s", ++initialPort, module));
                continue;
            } else {
                return initialPort;
            }
        }

        //不能保证20次尝试后仍然可能找到可用的端口；此时应用应该抛出异常
        return initialPort;
    }
}
