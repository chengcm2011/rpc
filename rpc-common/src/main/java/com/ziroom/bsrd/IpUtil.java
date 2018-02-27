package com.ziroom.bsrd;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * id
 *
 * @author chengys4
 *         2018-02-27 13:01
 **/
public class IpUtil {

    public static String getLocalIp() {
        // init address: ip : port
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
