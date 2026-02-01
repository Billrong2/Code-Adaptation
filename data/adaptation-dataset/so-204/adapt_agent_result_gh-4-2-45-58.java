private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        HttpCookie reconstructed = null;
        try {
            // Read fields in the exact order they were written
            String name = (String) in.readObject();
            String value = (String) in.readObject();

            // Enforce invariant: name and value must not be null
            if (name == null || value == null) {
                throw new IOException("Invalid cookie: name or value is null");
            }

            reconstructed = new HttpCookie(name, value);

            String comment = (String) in.readObject();
            String commentURL = (String) in.readObject();
            String domain = (String) in.readObject();
            long maxAge = in.readLong();
            String path = (String) in.readObject();
            String portList = (String) in.readObject();
            int version = in.readInt();
            boolean secure = in.readBoolean();
            boolean discard = in.readBoolean();

            // Apply optional properties with null checks
            if (comment != null) {
                reconstructed.setComment(comment);
            }
            if (commentURL != null) {
                reconstructed.setCommentURL(commentURL);
            }
            if (domain != null) {
                reconstructed.setDomain(domain);
            }
            reconstructed.setMaxAge(maxAge);
            if (path != null) {
                reconstructed.setPath(path);
            }
            if (portList != null) {
                reconstructed.setPortlist(portList);
            }
            reconstructed.setVersion(version);
            reconstructed.setSecure(secure);
            reconstructed.setDiscard(discard);

            // Store the reconstructed cookie as the client cookie
            this.mClientCookie = reconstructed;
        } catch (IOException | ClassNotFoundException e) {
            // Ensure object is left in a safe state on failure
            this.mClientCookie = null;
            throw e;
        }
    }