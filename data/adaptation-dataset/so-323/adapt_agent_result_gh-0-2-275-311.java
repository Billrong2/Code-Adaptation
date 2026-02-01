private boolean decodeHeader(Socket socket, BufferedReader in, Properties headers) throws InterruptedException {
      // Defensive checks
      if (in == null || headers == null) {
         try {
            sendError(socket, HTTP_BADREQUEST, null);
         } catch (InterruptedException ie) {
            throw ie;
         }
         return false;
      }

      try {
         // Read request line
         String requestLine = in.readLine();
         if (requestLine == null || requestLine.trim().length() == 0) {
            sendError(socket, HTTP_BADREQUEST, null);
            return false;
         }
         if (debug)
            log("Request: " + requestLine);

         // Tokenize request line: METHOD URI HTTP/VERSION
         StringTokenizer st = new StringTokenizer(requestLine);
         if (!st.hasMoreTokens()) {
            sendError(socket, HTTP_BADREQUEST, null);
            return false;
         }
         String method = st.nextToken();
         if (!"GET".equals(method)) {
            // Only GET is supported
            return false;
         }
         if (!st.hasMoreTokens()) {
            sendError(socket, HTTP_BADREQUEST, null);
            return false;
         }
         String uri = st.nextToken();
         if (uri == null || uri.length() == 0) {
            sendError(socket, HTTP_BADREQUEST, null);
            return false;
         }

         // Read headers until empty line or EOF
         String line;
         while ((line = in.readLine()) != null) {
            if (line.length() == 0)
               break; // end of headers
            int colon = line.indexOf(':');
            if (colon <= 0)
               continue; // ignore malformed header lines
            String key = line.substring(0, colon).trim().toLowerCase();
            String value = line.substring(colon + 1).trim();
            headers.setProperty(key, value);
            if (debug)
               log("Header: " + key + "=" + value);
         }

         return true;
      } catch (IOException ioe) {
         // IO error while reading request
         sendError(socket, HTTP_INTERNALERROR, null);
         return false;
      }
   }