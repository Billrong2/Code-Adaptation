private SMTPTransport connectToSmtp(String host, int port, String userEmail,
        String oauthToken, boolean debug) throws MessagingException {

    if (host == null || host.length() == 0)
        throw new MessagingException("SMTP host is null or empty");
    if (userEmail == null || userEmail.length() == 0)
        throw new MessagingException("User email is null or empty");
    if (oauthToken == null || oauthToken.length() == 0)
        throw new MessagingException("OAuth token is null or empty");

    final Properties props = new Properties();
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.starttls.required", "true");
    props.put("mail.smtp.sasl.enable", "false");
    props.put("mail.smtp.ssl.enable", "true");

    session = Session.getInstance(props);
    session.setDebug(debug);

    final URLName unusedUrlName = null;
    SMTPTransport transport = new SMTPTransport(session, unusedUrlName);

    // If the password is non-null, SMTP tries to do AUTH LOGIN.
    final String emptyPassword = null;
    transport.connect(host, port, userEmail, emptyPassword);

    final byte[] authBytes = String.format("user=%s\1auth=Bearer %s\1\1", userEmail,
            oauthToken).getBytes();
    final byte[] encodedResponse = BASE64EncoderStream.encode(authBytes);

    transport.issueCommand("AUTH XOAUTH2 " + new String(encodedResponse), 235);

    return transport;
}