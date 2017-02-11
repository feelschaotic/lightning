package com.ramo.wifi.lan;

public interface OnSearchListener {
	void onSuess(String ip, String host);

	void onError(Exception e);
}
