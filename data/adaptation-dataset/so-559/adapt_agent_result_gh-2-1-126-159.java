protected String[] getProtocolList() throws IOException
{
    String[] preferredProtocols = { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" };
    String[] availableProtocols = null;

    javax.net.ssl.SSLSocket socket = null;

    try
    {
        javax.net.ssl.SSLSocketFactory factory = mCtx.getSocketFactory();
        socket = (javax.net.ssl.SSLSocket) factory.createSocket();

        availableProtocols = socket.getSupportedProtocols();
        java.util.Arrays.sort(availableProtocols);
    }
    catch (Exception e)
    {
        return new String[]{ "TLSv1" };
    }
    finally
    {
        if (socket != null)
            socket.close();
    }

    java.util.List<String> aa = new java.util.ArrayList<String>();
    for (int i = 0; i < preferredProtocols.length; i++)
    {
        int idx = java.util.Arrays.binarySearch(availableProtocols, preferredProtocols[i]);
        if (idx >= 0)
            aa.add(preferredProtocols[i]);
    }

    return aa.toArray(new String[0]);
}