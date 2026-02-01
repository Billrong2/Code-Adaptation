public static void main(String[] args) throws Exception {
	// Build MM7 SubmitReq message
	final SubmitReq sr = new SubmitReq();
	sr.setNamespace("http://www.3gpp.org/ftp/Specs/archive/23_series/23.140/schema/REL-5-MM7-1-2");
	sr.setMm7Version("5.3.0");
	sr.setVaspId("Ejada");
	sr.setVasId("Ejada");
	sr.setSubject("Allah Akbar");
	sr.setDeliveryReport(true);
	sr.addRecipient(new Address("1111", RecipientType.TO));
	sr.setTransactionId("1348056868070-1-");

	// Load and validate resources
	final java.io.InputStream smilStream = load("smil-sample2.xml");	
	if (smilStream == null) {
		throw new IllegalArgumentException("Resource not found: smil-sample2.xml");
	}
	final java.io.InputStream imageStream = load("lorena.jpg");
	if (imageStream == null) {
		throw new IllegalArgumentException("Resource not found: lorena.jpg");
	}

	// Attach a SMIL presentation
	final BinaryContent smil = new BinaryContent("application/smil", smilStream);
	smil.setContentId("smil");

	// Attach a picture
	final BinaryContent image = new BinaryContent("image/jpeg", imageStream);
	image.setContentId("image");

	// Attach a text
	final TextContent text = new TextContent("Lorena sends a lots of hugs!");
	text.setContentId("text");

	// Pack it all up
	sr.setContent(new BasicContent(smil, image, text));

	// Serialize message to stdout
	MM7Message.save(sr, System.out, new MM7Context());
}