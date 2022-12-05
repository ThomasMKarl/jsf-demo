package de.onevision.network;

import de.onevision.Platform.Exceptions.Error;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Host {
    static public Host local() throws Error {
        Host host = new Host();
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
        }
        catch (UnknownHostException e) {
            throw new Error("internal error", "", "UnknownHostException");
        }
        host.ipAddr = ip.getAddress();
        host.hostName = ip.getHostName();

        return host;
    }

    public void ipFromString(String in) {
        String res[] = in.split(".");
        ipAddr[0] = Byte.parseByte(res[0]);
        ipAddr[1] = Byte.parseByte(res[1]);
        ipAddr[2] = Byte.parseByte(res[2]);
        ipAddr[3] = Byte.parseByte(res[3]);
    }

    public String ipAsString() {
        return Byte.toString(ipAddr[0]) + "." + Byte.toString(ipAddr[1]) + "." + Byte.toString(ipAddr[2]) + "." + Byte.toString(ipAddr[3]);
    }

    public String hostName = "localhost";
    public byte ipAddr[] = { 127, 0, 0, 1 };
    int port = 0;
}
