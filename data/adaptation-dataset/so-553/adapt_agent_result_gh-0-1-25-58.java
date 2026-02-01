public static void fileUrl(String sourceUrl, String destinationFilePath) {
	if (sourceUrl == null || sourceUrl.trim().isEmpty()) {
		throw new IllegalArgumentException("sourceUrl must not be null or empty");
	}
	if (destinationFilePath == null || destinationFilePath.trim().isEmpty()) {
		throw new IllegalArgumentException("destinationFilePath must not be null or empty");
	}

	try {
		URL url = new URL(sourceUrl);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(15000);
		connection.setReadTimeout(15000);

		byte[] buffer = new byte[size];
		try (InputStream in = connection.getInputStream();
			 OutputStream out = new BufferedOutputStream(new FileOutputStream(destinationFilePath))) {
			int bytesRead;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
		}

		System.out.println("Downloaded successfully to: " + destinationFilePath);
	} catch (java.net.MalformedURLException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}