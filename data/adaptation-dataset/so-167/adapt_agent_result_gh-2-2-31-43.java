private void writeObject(ObjectOutputStream out) throws IOException {
    if (out == null) {
        return;
    }
    // Serialize a single HttpCookie instance explicitly
    if (mCookie == null) {
        out.writeObject(null);
        out.writeObject(null);
        out.writeObject(null);
        out.writeObject(null);
        out.writeObject(null);
        out.writeLong(0L);
        out.writeObject(null);
        out.writeObject(null);
        out.writeInt(0);
        out.writeBoolean(false);
        out.writeBoolean(false);
        return;
    }
    out.writeObject(mCookie.getName());
    out.writeObject(mCookie.getValue());
    out.writeObject(mCookie.getComment());
    out.writeObject(mCookie.getCommentURL());
    out.writeObject(mCookie.getDomain());
    out.writeLong(mCookie.getMaxAge());
    out.writeObject(mCookie.getPath());
    out.writeObject(mCookie.getPortlist());
    out.writeInt(mCookie.getVersion());
    out.writeBoolean(mCookie.getSecure());
    out.writeBoolean(mCookie.getDiscard());
    out.flush();
}