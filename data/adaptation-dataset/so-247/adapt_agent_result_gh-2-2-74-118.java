public String sendPost(String url, String data, String contentType) {
        ret = null;

        httpClient.getParams().setParameter(org.apache.http.client.params.ClientPNames.COOKIE_POLICY, org.apache.http.client.params.CookiePolicy.RFC_2109);

        httpPost = new org.apache.http.client.methods.HttpPost(url);
        response = null;

        org.apache.http.entity.StringEntity tmp = null;

        android.util.Log.d("Your App Name Here", "Setting httpPost headers");

        httpPost.setHeader("User-Agent", "SET YOUR USER AGENT STRING HERE");
        httpPost.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*;q=0.5");

        if (contentType != null) {
            httpPost.setHeader("Content-Type", contentType);
        } else {
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        }

        try {
            tmp = new org.apache.http.entity.StringEntity(data, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            android.util.Log.e("Your App Name Here", "HttpUtils : UnsupportedEncodingException : " + e);
        }

        httpPost.setEntity(tmp);

        android.util.Log.d("Your App Name Here", url + "?" + data);

        try {
            response = httpClient.execute(httpPost, localContext);

            if (response != null) {
                ret = org.apache.http.util.EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            android.util.Log.e("Your App Name Here", "HttpUtils: " + e);
        }

        android.util.Log.d("Your App Name Here", "Returning value:" + ret);

        return ret;
    }