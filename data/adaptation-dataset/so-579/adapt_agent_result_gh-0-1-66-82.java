    @Override
    protected void authenticate() throws IOException, XMPPException {
        final String localAuthId = this.authenticationId;
        final String localPassword = this.password;

        if (localAuthId == null || localAuthId.length() == 0) {
            throw new IllegalArgumentException("authenticationId must not be null or empty");
        }
        if (localPassword == null || localPassword.length() == 0) {
            throw new IllegalArgumentException("password must not be null or empty");
        }

        try {
            // SASL payload: username NUL password (no authCode generation)
            final String payload = "\0" + java.net.URLEncoder.encode(localAuthId, "UTF-8") + "\0" + localPassword;

            // Use Base64 encoder that returns byte[] and then convert to String with explicit charset
            final byte[] encodedBytes = org.apache.commons.codec.binary.Base64.encodeBase64(payload.getBytes("UTF-8"));
            final String encodedPayload = new String(encodedBytes, "UTF-8");

            // Construct and send auth using Auth2Mechanism packet instead of raw XML string
            Auth2Mechanism authPacket = new Auth2Mechanism(
                    "<auth mechanism=\"" + getName() + "\" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">"
                            + encodedPayload + "</auth>");

            // Send the authentication to the server
            getSASLAuthentication().send(authPacket);

            // Log.d(NAME, "SASL authentication packet sent");
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            // Wrap any unexpected exception as IOException to match method contract
            IOException ioe = new IOException("Failed to perform SASL authentication");
            ioe.initCause(e);
            throw ioe;
        }
    }