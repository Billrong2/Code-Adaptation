protected void authenticate() throws java.io.IOException, org.jivesoftware.smack.XMPPException {
    // Basic validation
    if (this.authenticationId == null || this.authenticationId.length() == 0) {
        throw new IllegalArgumentException("authenticationId must not be null or empty");
    }
    if (this.password == null || this.password.length() == 0) {
        throw new IllegalArgumentException("password must not be null or empty");
    }

    final String jidAndToken = "\0" + java.net.URLEncoder.encode(this.authenticationId, "UTF-8") + "\0" + this.password;

    final StringBuilder stanza = new StringBuilder();
    stanza.append("<auth mechanism=\"").append(getName());
    stanza.append("\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">");

    final byte[] encoded = org.apache.commons.codec.binary.Base64.encodeBase64(jidAndToken.getBytes("UTF-8"));
    stanza.append(new String(encoded, "UTF-8"));
    stanza.append("</auth>");

    // Log.v(NAME, "Authentication text is " + stanza); // intentionally disabled to avoid leaking sensitive data

    // Send the authentication to the server
    getSASLAuthentication().send(new Auth2Mechanism(stanza.toString()));
  }