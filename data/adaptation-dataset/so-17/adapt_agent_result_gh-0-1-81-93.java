private void sendToServer(String stacktrace, String filename) {
	// validate inputs
	if (this.url == null || this.url.length() == 0 || filename == null || filename.length() == 0 || stacktrace == null || stacktrace.length() == 0) {
		return;
	}

	org.apache.http.impl.client.DefaultHttpClient httpClient = null;
	org.apache.http.HttpResponse response = null;
	try {
		httpClient = new org.apache.http.impl.client.DefaultHttpClient();
		org.apache.http.client.methods.HttpPost httpPost = new org.apache.http.client.methods.HttpPost(this.url);

		java.util.List<org.apache.http.NameValuePair> params = new java.util.ArrayList<org.apache.http.NameValuePair>();
		params.add(new org.apache.http.message.BasicNameValuePair("filename", filename));
		params.add(new org.apache.http.message.BasicNameValuePair("stacktrace", stacktrace));

		org.apache.http.client.entity.UrlEncodedFormEntity entity =
			new org.apache.http.client.entity.UrlEncodedFormEntity(params, org.apache.http.protocol.HTTP.UTF_8);
		httpPost.setEntity(entity);

		response = httpClient.execute(httpPost);
		if (response != null && response.getEntity() != null) {
			// ensure the response content is fully consumed
			response.getEntity().consumeContent();
		}
	} catch (java.io.IOException ioe) {
		ioe.printStackTrace();
	} finally {
		if (httpClient != null) {
			httpClient.getConnectionManager().shutdown();
		}
	}
}