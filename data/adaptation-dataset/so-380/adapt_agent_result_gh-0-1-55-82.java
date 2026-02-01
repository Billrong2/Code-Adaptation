public void getCSVAsFile(String urlString, File csvFile) throws IOException {
	client = ApacheHttpClient.create();
	WebResource webResource = client.resource(urlString);
	WebResource.Builder builder = webResource.accept(
			"application/json,application/pdf,text/plain,image/jpeg,application/xml,application/vnd.ms-excel");

	ClientResponse response = builder.get(ClientResponse.class);
	if (response.getStatus() != 200) {
		throw new RuntimeException("HTTP error code : " + response.getStatus());
	}

	try (InputStream input = response.getEntity(InputStream.class);
			FileOutputStream fos = new FileOutputStream(csvFile)) {
		byte[] byteArray = org.apache.commons.io.IOUtils.toByteArray(input);
		fos.write(byteArray);
		fos.flush();
	}
}