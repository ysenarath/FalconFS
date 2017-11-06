package lk.uomcse.fs.utils;

import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPUtils {
    /**
     * Returns public ip addresses (including localhost)
     *
     * @return public ip addresses
     */
    public static List<String> getPublicIpAddress() {
        List<String> res = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e.nextElement();
                if (ni.isLoopback())
                    continue;
                if (ni.isPointToPoint())
                    continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if (address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        res.add(ip);
                    }
                }
            }
        } catch (SocketException e) {
            String localhost = null;
            try {
                localhost = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e1) {
                return new ArrayList<>();
            }
            res = new ArrayList<>();
            res.add(localhost);
            return res;
        }
        return res;
    }
}
