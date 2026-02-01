private String readFromInternalFile(String name) {
    if (context == null) {
        Log.e("PersistentData", "readFromInternalFile: context is null");
        return "";
    }
    if (name == null || name.length() == 0) {
        Log.e("PersistentData", "readFromInternalFile: filename is null or empty");
        return "";
    }

    String sanitizedName = escapePathSeparator(name);
    StringBuilder stringBuilder = new StringBuilder();

    try (java.io.InputStream inputStream = context.openFileInput(sanitizedName);
         java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8);
         java.io.BufferedReader bufferedReader = new java.io.BufferedReader(inputStreamReader)) {

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
    } catch (java.io.IOException e) {
        Log.e("PersistentData", "readFromInternalFile: cannot read file '" + sanitizedName + "': " + e.toString());
    }

    return stringBuilder.toString();
}