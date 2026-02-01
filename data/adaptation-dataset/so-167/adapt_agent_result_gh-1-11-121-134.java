private void writeObject(ObjectOutputStream out) throws IOException {
        if (cookie == null) {
            throw new IOException("HttpCookie is null and cannot be serialized");
        }
        String name = cookie.getName();
        String value = cookie.getValue();
        if (name == null || value == null) {
            throw new IOException("HttpCookie name/value must not be null");
        }
        out.writeObject(name);
        out.writeObject(value);
        out.writeObject(cookie.getComment());
        out.writeObject(cookie.getCommentURL());
        out.writeObject(cookie.getDomain());
        out.writeLong(cookie.getMaxAge());
        out.writeObject(cookie.getPath());
        out.writeObject(cookie.getPortlist());
        out.writeInt(cookie.getVersion());
        out.writeBoolean(cookie.getSecure());
        out.writeBoolean(cookie.getDiscard());
        out.writeBoolean(getHttpOnly());
    }