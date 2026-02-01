private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Deserialize fields in the same order as writeObject
        String name = (String) in.readObject();
        String value = (String) in.readObject();
        String comment = (String) in.readObject();
        String commentURL = (String) in.readObject();
        String domain = (String) in.readObject();
        long maxAge = in.readLong();
        String path = (String) in.readObject();
        String portList = (String) in.readObject();
        int version = in.readInt();
        boolean secure = in.readBoolean();
        boolean discard = in.readBoolean();
        boolean httpOnly = in.readBoolean();

        if (name == null || value == null) {
            throw new IOException("Invalid serialized HttpCookie: name/value is null");
        }

        // Reconstruct single HttpCookie instance
        cookie = new HttpCookie(name, value);

        try {
            if (comment != null) cookie.setComment(comment);
            if (commentURL != null) cookie.setCommentURL(commentURL);
            if (domain != null && domain.length() > 0) cookie.setDomain(domain);
            cookie.setMaxAge(maxAge);
            if (path != null && path.length() > 0) cookie.setPath(path);
            if (portList != null) cookie.setPortlist(portList);
            cookie.setVersion(version);
            cookie.setSecure(secure);
            cookie.setDiscard(discard);
            // Restore httpOnly via reflection workaround
            setHttpOnly(httpOnly);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "Failed to restore some HttpCookie attributes", e);
        }
    }