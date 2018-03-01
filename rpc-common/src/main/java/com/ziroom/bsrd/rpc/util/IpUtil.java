package com.ziroom.bsrd.rpc.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpUtil {

    public static String getLocalIp() {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        if (ip == null) {
            return "";
        }
        return ip;
    }

}
