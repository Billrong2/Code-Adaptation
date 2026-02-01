private void handleResponse(Socket socket) throws IOException {
      if (socket == null || socket.isClosed())
         return;

      Properties headers = new Properties();
      byte[] buf = new byte[8192];
      long start = 0;
      long end = -1;
      boolean partial = false;

      try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
         if (!decodeHeader(socket, in, headers))
            return;

         String range = headers.getProperty("range");
         Properties respHeaders = new Properties();

         if (canSeek && range != null && range.startsWith("bytes=")) {
            try {
               String spec = range.substring(6).trim();
               int dash = spec.indexOf('-');
               if (dash >= 0) {
                  String s = spec.substring(0, dash).trim();
                  String e = spec.substring(dash + 1).trim();
                  if (!s.isEmpty())
                     start = Long.parseLong(s);
                  if (!e.isEmpty())
                     end = Long.parseLong(e);
               }

               if (start < 0 || start >= fileSize)
                  sendError(socket, HTTP_416, "Range out of bounds");

               if (end < 0 || end >= fileSize)
                  end = fileSize - 1;

               if (end < start)
                  sendError(socket, HTTP_416, "Invalid Range");

               long length = end - start + 1;
               if (length > Integer.MAX_VALUE)
                  sendError(socket, HTTP_416, "Range too large");

               ((RandomAccessInputStream)is).seek(start);
               respHeaders.setProperty("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
               respHeaders.setProperty("Content-Length", String.valueOf(length));
               respHeaders.setProperty("Accept-Ranges", "bytes");

               sendResponse(socket, "206 Partial Content", fileMimeType, respHeaders, is, (int)length, buf, null);
               partial = true;
            } catch (NumberFormatException nfe) {
               sendError(socket, HTTP_416, "Invalid Range format");
            }
         }

         if (!partial) {
            if (fileSize >= 0 && fileSize <= Integer.MAX_VALUE)
               respHeaders.setProperty("Content-Length", String.valueOf(fileSize));
            respHeaders.setProperty("Accept-Ranges", canSeek ? "bytes" : "none");
            sendResponse(socket, "200 OK", fileMimeType, respHeaders, is, (int)Math.min(fileSize, Integer.MAX_VALUE), buf, null);
         }
      } catch (InterruptedException ie) {
         try {
            sendError(socket, HTTP_INTERNALERROR, "Request interrupted");
         } catch (InterruptedException ignored) {
         }
      } catch (IOException ioe) {
         try {
            sendError(socket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
         } catch (InterruptedException ignored) {
         }
      }
   }