/**
     * Extracts host name from the given URL string.
     * <p>
     * For example, a URL like {@code http://www.stackoverflow.com/questions}
     * will return {@code www.stackoverflow.com}.
     * </p>
     *
     * @param url URL string to extract host from; if {@code null} or empty, an empty string is returned
     * @return Host part of the URL without protocol, port, or path, or empty string if input is {@code null} or empty
     * @author Stack Overflow contributors
     * @source https://stackoverflow.com/
     */
    public static String getHost(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }

        int doubleSlash = url.indexOf("//");
        if (doubleSlash == -1) {
            doubleSlash = 0;
        } else {
            doubleSlash += 2;
        }

        int end = url.indexOf('/', doubleSlash);
        end = end >= 0 ? end : url.length();

        int port = url.indexOf(':', doubleSlash);
        end = (port > 0 && port < end) ? port : end;

        return url.substring(doubleSlash, end);
    }