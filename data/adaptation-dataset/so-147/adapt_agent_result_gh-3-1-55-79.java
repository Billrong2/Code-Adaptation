private void setupProxyAuthenticator()
{
    try
    {
        Authenticator.setDefault(new Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                if (getRequestorType() != RequestorType.PROXY)
                    return null;

                final String requestingProtocol = getRequestingProtocol();
                final String requestingHost = getRequestingHost();
                if (requestingProtocol == null || requestingHost == null)
                    return null;

                final String protocol = requestingProtocol.toLowerCase();
                final String proxyHost = System.getProperty(protocol + ".proxyHost");
                final String proxyPortValue = System.getProperty(protocol + ".proxyPort");
                final String proxyUser = System.getProperty(protocol + ".proxyUser");
                final String proxyPassword = System.getProperty(protocol + ".proxyPassword");

                if (proxyHost == null || proxyHost.isEmpty() || proxyUser == null || proxyUser.isEmpty())
                    return null;

                final int proxyPort;
                try
                {
                    proxyPort = proxyPortValue != null ? Integer.parseInt(proxyPortValue) : -1;
                }
                catch (NumberFormatException e)
                {
                    return null;
                }

                if (!requestingHost.equalsIgnoreCase(proxyHost))
                    return null;

                if (proxyPort >= 0 && proxyPort != getRequestingPort())
                    return null;

                final char[] passwordChars = proxyPassword != null ? proxyPassword.toCharArray() : new char[0];
                return new PasswordAuthentication(proxyUser, passwordChars);
            }
        });
    }
    catch (SecurityException e)
    {
        log(e);
    }
}