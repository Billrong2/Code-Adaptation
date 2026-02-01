@SuppressWarnings("nls")
private void setupProxyAuthenticator()
{
    java.net.Authenticator.setDefault(new java.net.Authenticator()
    {
        @Override
        protected java.net.PasswordAuthentication getPasswordAuthentication()
        {
            if (getRequestorType() == java.net.Authenticator.RequestorType.PROXY)
            {
                final String protocol = getRequestingProtocol();
                if (protocol == null || protocol.isEmpty())
                    return null;

                final String protocolKey = protocol.toLowerCase(java.util.Locale.ROOT);
                final String host = System.getProperty(protocolKey + ".proxyHost", "");
                final String port = System.getProperty(protocolKey + ".proxyPort", "80");
                final String user = System.getProperty(protocolKey + ".proxyUser", "");
                final String password = System.getProperty(protocolKey + ".proxyPassword", "");

                if (!host.isEmpty() && getRequestingHost() != null && getRequestingHost().equalsIgnoreCase(host))
                {
                    try
                    {
                        final int proxyPort = Integer.parseInt(port);
                        if (proxyPort == getRequestingPort())
                            return new java.net.PasswordAuthentication(user, password.toCharArray());
                    }
                    catch (NumberFormatException e)
                    {
                        return null;
                    }
                }
            }
            return null;
        }
    });
}