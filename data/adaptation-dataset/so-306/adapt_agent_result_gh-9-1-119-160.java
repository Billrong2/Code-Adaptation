    /**
     * Reads and returns the request body as a String.
     * 
     * @param request the HttpServletRequest to read data from
     * @return the request body, or an empty string if no input stream is available
     * @throws IOException declared for compatibility; IOExceptions are logged and suppressed internally
     */
    public static String getRequestData(HttpServletRequest request) throws IOException {
        // Adapted from a common Stack Overflow example for reading servlet request bodies
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        ServletInputStream servletInputStream = null;
        InputStreamReader inputStreamReader = null;
        final int bufferSize = 128;

        if (request == null) {
            return "";
        }

        try {
            servletInputStream = request.getInputStream();
            if (servletInputStream != null) {
                inputStreamReader = new InputStreamReader(servletInputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                char[] charBuffer = new char[bufferSize];
                int charsRead;
                while ((charsRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, charsRead);
                }
            } else {
                return "";
            }
        } catch (IOException ex) {
            logger.log(Level.INFO, "IOException while reading request data", ex);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    logger.log(Level.INFO, "IOException while closing request reader", ex);
                }
            }
        }

        return stringBuilder.toString();
    }