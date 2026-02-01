private String getMmsText(String id) {
        if (id == null || _context == null) {
            return "";
        }
        InputStream is = null;
        BufferedReader reader = null;
        StringBuilder body = new StringBuilder();
        try {
            Uri partUri = Uri.withAppendedPath(MMS_PART_CONTENT_URI, id);
            if (partUri == null || _context.getContentResolver() == null) {
                return "";
            }
            is = _context.getContentResolver().openInputStream(partUri);
            if (is == null) {
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        } catch (IOException e) {
            Log.error("Error reading MMS text part (id=" + id + ")", e);
            return "";
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
        return body.toString();
    }