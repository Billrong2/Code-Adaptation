  /**
   * Returns an {@link java.net.InetAddress} object encapsulating what is most likely the machine's LAN IP address.
   * <p>
   * This method is intended as a safer replacement for {@link java.net.InetAddress#getLocalHost()}, which can be
   * ambiguous on some systems (notably Linux) where loopback interfaces may be selected.
   * <p>
   * Selection algorithm:
   * <ol>
   *   <li>Fast-path: return {@code InetAddress.getLocalHost()} if it is non-loopback and site-local.</li>
   *   <li>Otherwise, scan all network interfaces and prefer the first non-loopback site-local address.</li>
   *   <li>If none found, return the first non-loopback address as a candidate.</li>
   *   <li>Final fallback to {@code InetAddress.getLocalHost()}.</li>
   * </ol>
   * <p>
   * See also: https://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4665037
   *
   * @throws java.net.UnknownHostException if the LAN address of the machine cannot be determined
   */
  static InetAddress discoverLANAddress() throws UnknownHostException {
    // Fast-path: let UnknownHostException propagate directly
    final InetAddress localHost = InetAddress.getLocalHost();
    if (localHost != null && !localHost.isLoopbackAddress() && localHost.isSiteLocalAddress()) {
      return localHost;
    }

    try {
      InetAddress candidateAddress = null;

      final Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
      if (ifaces != null) {
        // Iterate all NICs (network interface cards)...
        while (ifaces.hasMoreElements()) {
          final NetworkInterface iface = ifaces.nextElement();
          if (iface == null) {
            continue;
          }
          try {
            if (!iface.isUp()) {
              continue;
            }
          } catch (Exception ignored) {
            // Preserve original behavior if interface state cannot be determined
          }

          final Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
          if (inetAddrs == null) {
            continue;
          }
          // Iterate all IP addresses assigned to each card...
          while (inetAddrs.hasMoreElements()) {
            final InetAddress inetAddr = inetAddrs.nextElement();
            if (inetAddr == null || inetAddr.isLoopbackAddress()) {
              continue;
            }

            if (inetAddr.isSiteLocalAddress()) {
              // Found non-loopback site-local address. Return it immediately...
              return inetAddr;
            } else if (candidateAddress == null) {
              // Found non-loopback address, but not necessarily site-local.
              candidateAddress = inetAddr;
            }
          }
        }
      }

      if (candidateAddress != null) {
        // We did not find a site-local address, but we found some other non-loopback address.
        return candidateAddress;
      }

      // Final fallback to whatever InetAddress.getLocalHost() returns...
      final InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
      if (jdkSuppliedAddress == null) {
        throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
      }
      return jdkSuppliedAddress;
    } catch (Exception e) {
      final UnknownHostException unknownHostException =
          new UnknownHostException("Failed to determine LAN address: " + e);
      unknownHostException.initCause(e);
      throw unknownHostException;
    }
  }