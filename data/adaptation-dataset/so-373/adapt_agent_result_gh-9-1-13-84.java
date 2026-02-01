/**
 * Returns an {@link java.net.InetAddress} object encapsulating what is most likely the machine's LAN IP address.
 * <p/>
 * This method is intended as a replacement for {@link java.net.InetAddress#getLocalHost()} because that method
 * can be ambiguous on Linux systems where loopback interfaces may be enumerated before real LAN interfaces.
 * <p/>
 * Source: Adapted from a Stack Overflow answer discussing reliable LAN address detection
 * (original author and discussion credited to Stack Overflow community).
 * <p/>
 * @return the most likely LAN {@link java.net.InetAddress}
 * @throws java.net.UnknownHostException if the LAN address of the machine cannot be determined
 */
public static java.net.InetAddress getLocalHostLANAddress() throws java.net.UnknownHostException {
    try {
        java.net.InetAddress candidateAddress = null;
        // Iterate all NICs (network interface cards)...
        for (final java.util.Enumeration<java.net.NetworkInterface> ifaces = java.net.NetworkInterface.getNetworkInterfaces();
             ifaces != null && ifaces.hasMoreElements(); ) {
            final java.net.NetworkInterface iface = ifaces.nextElement();
            // Iterate all IP addresses assigned to each card...
            for (final java.util.Enumeration<java.net.InetAddress> inetAddrs = iface.getInetAddresses();
                 inetAddrs != null && inetAddrs.hasMoreElements(); ) {
                final java.net.InetAddress inetAddr = inetAddrs.nextElement();
                if (!inetAddr.isLoopbackAddress()) {
                    if (inetAddr.isSiteLocalAddress()) {
                        // Found non-loopback site-local address. Return it immediately...
                        return inetAddr;
                    } else if (candidateAddress == null) {
                        // Found non-loopback address, but not necessarily site-local.
                        // Store it as a candidate to be returned if a site-local address is not found...
                        candidateAddress = inetAddr;
                    }
                }
            }
        }
        if (candidateAddress != null) {
            // Return the first non-loopback, non-site-local address found...
            return candidateAddress;
        }
        // Fall back to whatever InetAddress.getLocalHost() returns...
        final java.net.InetAddress jdkSuppliedAddress = java.net.InetAddress.getLocalHost();
        if (jdkSuppliedAddress == null) {
            throw new java.net.UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
        }
        return jdkSuppliedAddress;
    } catch (Exception e) {
        final java.net.UnknownHostException unknownHostException =
                new java.net.UnknownHostException("Failed to determine LAN address: " + e);
        unknownHostException.initCause(e);
        throw unknownHostException;
    }
}