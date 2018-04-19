package com.pft.communicate.core.support.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetInterfaceUtil {

	public static String getIdentifyAddress() {
		List<InetAddress> internetAddress;
		try {
			internetAddress = getInternetAddress();
		} catch (SocketException e) {
			return "0.0.0.1";
		}

		if (internetAddress.size() == 0) {
			return "127.0.0.1";
		}

		return internetAddress.get(0).getHostAddress();
	}

	public static List<InetAddress> getInternetAddress() throws SocketException {

		List<InetAddress> address = new ArrayList<InetAddress>();

		Enumeration<?> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		while (allNetInterfaces.hasMoreElements()) {
			NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
			String interfaceName = netInterface.getName();
			if (interfaceName.contains("docker") || "lo".equals(interfaceName)) {
				continue;
			}
			Enumeration<?> addresses = netInterface.getInetAddresses();
			while (addresses.hasMoreElements()) {
				ip = (InetAddress) addresses.nextElement();
				if (ip != null && ip instanceof Inet4Address) {
					if (!ip.isLoopbackAddress() && !ip.isMulticastAddress() && ip.getHostAddress().indexOf(":") == -1) {
						address.add(ip);
					}
				}
			}
		}

		return address;

	}

}
