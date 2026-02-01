public static boolean isConnectionFast(int type, int subType)
{
	// Treat Wi‑Fi, Ethernet and WiMAX as fast connections
	int ethernetType = -1;
	int wimaxType = -1;
	try
	{
		// Defensive reflection to avoid issues if constants are unavailable at compile/runtime
		java.lang.reflect.Field f = android.net.ConnectivityManager.class.getField("TYPE_ETHERNET");
		ethernetType = f.getInt(null);
	}
	catch(Exception ignore) {}
	try
	{
		java.lang.reflect.Field f = android.net.ConnectivityManager.class.getField("TYPE_WIMAX");
		wimaxType = f.getInt(null);
	}
	catch(Exception ignore) {}

	if(type == android.net.ConnectivityManager.TYPE_WIFI || type == ethernetType || type == wimaxType)
		return true;

	if(type == android.net.ConnectivityManager.TYPE_MOBILE)
	{
		switch(subType)
		{
			case android.telephony.TelephonyManager.NETWORK_TYPE_1xRTT:
				return false; // ~ 50-100 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_CDMA:
				return false; // ~ 14-64 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_EDGE:
				return false; // ~ 50-100 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_0:
				return true; // ~ 400-1000 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_A:
				return true; // ~ 600-1400 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_GPRS:
				return false; // ~ 100 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_HSDPA:
				return true; // ~ 2-14 Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_HSPA:
				return true; // ~ 700-1700 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_HSUPA:
				return true; // ~ 1-23 Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_UMTS:
				return true; // ~ 400-7000 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
				return true; // ~ 1-2 Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
				return true; // ~ 5 Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
				return true; // ~ 10-20 Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
				return false; // ~25 kbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_LTE: // API level 11
				return true; // ~ 10+ Mbps
			case android.telephony.TelephonyManager.NETWORK_TYPE_UNKNOWN:
			default:
				return false;
		}
	}

	// Unknown or unsupported network types are treated as slow
	return false;
}