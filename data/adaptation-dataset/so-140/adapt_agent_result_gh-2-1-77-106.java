private String getOriginalURL(final javax.servlet.http.HttpServletRequest req) {
        // Rebuilds the original request URL (scheme, host, port, paths, query)
        if (req == null) {
            return "";
        }

        final String scheme = req.getScheme();             // http
        final String serverName = req.getServerName();     // hostname.com
        final int serverPort = req.getServerPort();        // 80
        final String contextPath = req.getContextPath();   // /mywebapp
        final String servletPath = req.getServletPath();   // /servlet/MyServlet
        final String pathInfo = req.getPathInfo();         // /a/b;c=123
        final String queryString = req.getQueryString();   // d=789

        final StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath).append(servletPath);

        if (pathInfo != null) {
            url.append(pathInfo);
        }
        if (queryString != null) {
            url.append("?").append(queryString);
        }
        return url.toString();
    }