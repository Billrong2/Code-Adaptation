private String loadAssetAsString(String path) {
        StringBuilder buffer = new StringBuilder();
        if (path == null || path.length() == 0) {
            return buffer.toString();
        }
        try (java.io.InputStream is = assets.open(path);
             java.io.BufferedReader reader = new java.io.BufferedReader(
                     new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    buffer.append('\n');
                }
                buffer.append(line);
            }
        } catch (java.io.IOException e) {
            Log.d(CampusMapActivity.class.getSimpleName(),
                    "Exception while reading asset contents.");
        }
        return buffer.toString();
    }