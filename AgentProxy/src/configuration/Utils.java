package configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Utils {

	public static String getMyIp() {
		InetAddress inetAdd = null;
		try {
			inetAdd = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		String ip = null;
		if(inetAdd != null) {
			ip = inetAdd.getHostAddress();
		}
		return ip;
	}
}
