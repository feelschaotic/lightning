package com.ramo.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.reflect.Method;

public class HPaConnector {

	private static final String SETUP_WIFIAP_METHOD = "setWifiApEnabled";
	Context context = null;
	WifiManager wifiManager = null;
	static HPaConnector hPaConnector = null;

	public static HPaConnector getInstance(Context context) {
		if (hPaConnector == null) {
			hPaConnector = new HPaConnector();
			hPaConnector.context = context.getApplicationContext();
			hPaConnector.wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		}
		return hPaConnector;
	}

	public void setupWifiAp(String name, String password) throws Exception {

		if (name == null || "".equals(name)) {
			throw new Exception("the name of the wifiap is cannot be null");
		}
		Method setupMethod = wifiManager.getClass().getMethod(SETUP_WIFIAP_METHOD, WifiConfiguration.class, boolean.class);

		WifiConfiguration netConfig = new WifiConfiguration();
		// 设置wifi热点名称
		netConfig.SSID = name;

		netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

		if (password != null) {
			if (password.length() < 8) {
				throw new Exception("the length of wifi password must be 8 or longer");
			}
			// 设置wifi热点密码
			netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			netConfig.preSharedKey = password;
		}

		setupMethod.invoke(wifiManager, netConfig, true);
	}

	public void setupWifiAp(String name) {
		setupWifiAp(name);
	}
}
