@SuppressWarnings("deprecation")
    private static void ClearCookies(android.content.Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            com.janrain.android.utils.LogUtils.logd("Clearing cookies using modern API path (API >= " + android.os.Build.VERSION_CODES.LOLLIPOP + ")");
            android.webkit.CookieManager.getInstance().removeAllCookies(null);
            android.webkit.CookieManager.getInstance().flush();
        } else {
            com.janrain.android.utils.LogUtils.logd("Clearing cookies using legacy API path (API < " + android.os.Build.VERSION_CODES.LOLLIPOP + ")");
            android.webkit.CookieSyncManager cookieSyncManager = android.webkit.CookieSyncManager.createInstance(context);
            cookieSyncManager.startSync();
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncManager.stopSync();
            cookieSyncManager.sync();
        }
    }