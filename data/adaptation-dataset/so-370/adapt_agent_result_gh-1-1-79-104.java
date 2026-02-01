@Override
public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
	ParcelFileDescriptor[] pipe = null;
	HttpURLConnection connection = null;
	InputStream remoteInputStream = null;

	try {
		if (uri == null || uri.getPath() == null || uri.getPath().length() == 0) {
			throw new FileNotFoundException("Invalid URI: " + uri);
		}

		// Derive URL from URI path: strip leading slash and URL-decode (UTF-8)
		String path = uri.getPath();
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		final String decodedUrl = URLDecoder.decode(path, "UTF-8");

		final URL url = new URL(decodedUrl);
		connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(20000);
		connection.setDoInput(true);
		connection.connect();

		remoteInputStream = connection.getInputStream();

		pipe = ParcelFileDescriptor.createPipe();
		final ParcelFileDescriptor.AutoCloseOutputStream out =
				new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]);

		new TransferThread(remoteInputStream, out).start();
	} catch (java.net.MalformedURLException e) {
		Log.e(getClass().getSimpleName(), "Malformed URL for remote resource", e);
		throw new FileNotFoundException("Invalid URL for: " + uri);
	} catch (java.io.UnsupportedEncodingException e) {
		Log.e(getClass().getSimpleName(), "Unsupported encoding decoding URL", e);
		throw new FileNotFoundException("Could not decode URL for: " + uri);
	} catch (IOException e) {
		Log.e(getClass().getSimpleName(), "Exception opening HTTP pipe", e);
		// Cleanup on failure
		if (remoteInputStream != null) {
			try { remoteInputStream.close(); } catch (IOException ignored) {}
		}
		if (connection != null) {
			connection.disconnect();
		}
		throw new FileNotFoundException("Could not open pipe for: " + uri);
	}

	return pipe[0];
}