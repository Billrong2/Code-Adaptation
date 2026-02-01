@Override
public void setEnabledProtocols(String[] protocols) {
    final String SSLV2 = "SSLv2";
    final String SSLV3 = "SSLv3";

    // Only apply workaround when the input is exactly ["SSLv3"]
    if (protocols != null && protocols.length == 1 && SSLV3.equals(protocols[0])) {
        // Choose base protocol list depending on compatibility flag
        String[] baseProtocols = compatible ? delegate.getEnabledProtocols()
                                            : delegate.getSupportedProtocols();

        if (baseProtocols != null && baseProtocols.length > 0) {
            final java.util.List<String> adjusted = new java.util.ArrayList<String>(
                    java.util.Arrays.asList(baseProtocols));

            if (adjusted.size() > 1) {
                // Remove legacy SSL protocols when multiple choices are available
                adjusted.remove(SSLV2);
                adjusted.remove(SSLV3);
            }

            if (adjusted.size() <= 1) {
                android.util.Log.w(TAG,
                        "SSL stuck with single protocol available: " + String.valueOf(adjusted));
            }

            protocols = adjusted.toArray(new String[adjusted.size()]);
        }
    }

    super.setEnabledProtocols(protocols);
}