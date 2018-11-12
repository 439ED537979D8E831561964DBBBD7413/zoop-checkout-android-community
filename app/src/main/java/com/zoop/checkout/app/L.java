package com.zoop.checkout.app;


import android.util.Log;

public class L {
	
	public static void d(String message) {
		Log.d(ApplicationConfiguration.APP_DESCRIPTOR, message);
	}
	
	public static void e(String message, Exception e) {
		Log.e(ApplicationConfiguration.APP_DESCRIPTOR, message, e);
	}
	

}
