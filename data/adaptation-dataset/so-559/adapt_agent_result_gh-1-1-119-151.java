protected String[] GetProtocolList() {
    final String[] preferredProtocols = { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
    String[] availableProtocols = null;

    SSLSocket socket = null;
    try {
        SSLSocketFactory factory = m_ctx.getSocketFactory();
        socket = (SSLSocket) factory.createSocket();

        availableProtocols = socket.getSupportedProtocols();
        if (availableProtocols != null) {
            Arrays.sort(availableProtocols);
        }
    } catch (Exception e) {
        // Fallback to a minimal, widely supported protocol set
        return new String[]{ "TLSv1" };
    } finally {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ignore) {
                // Ignore close failures so they do not mask earlier exceptions
            }
        }
    }

    if (availableProtocols == null) {
        return new String[]{ "TLSv1" };
    }

    List<String> enabledProtocols = new ArrayList<String>();
    for (int i = 0; i < preferredProtocols.length; i++) {
        int idx = Arrays.binarySearch(availableProtocols, preferredProtocols[i]);
        if (idx >= 0) {
            enabledProtocols.add(preferredProtocols[i]);
        }
    }

    return enabledProtocols.toArray(new String[0]);
}