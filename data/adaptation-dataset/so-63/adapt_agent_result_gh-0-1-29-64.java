public void saveFile(String selectedRoute) {
    File targetFile = null;
    try {
        if (selectedRoute == null || selectedRoute.trim().isEmpty()) {
            Log.d("InputSave", "Selected route is null or empty; aborting saveFile");
            return;
        }

        String urlString = PortAuthorityAPI.getPatterns(selectedRoute);
        if (urlString == null || urlString.trim().isEmpty()) {
            Log.d("InputSave", "No URL returned for route " + selectedRoute);
            return;
        }

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.connect();
        int lengthOfFile = connection.getContentLength();

        File baseDir = new File(context.getFilesDir(), "lineinfo");
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        targetFile = new File(baseDir, selectedRoute + ".xml");

        if (targetFile.exists()) {
            Log.d("InputSave", "File already exists for route " + selectedRoute + "; skipping download");
            return;
        }

        byte[] data = new byte[1024];
        int count;
        long total = 0;
        int progress = 0;

        try (InputStream is = url.openStream(); FileOutputStream fos = new FileOutputStream(targetFile)) {
            while ((count = is.read(data)) != -1) {
                total += count;
                if (lengthOfFile > 0) {
                    int progressTemp = (int) (total * 100 / lengthOfFile);
                    if (progressTemp % 10 == 0 && progress != progressTemp) {
                        progress = progressTemp;
                    }
                }
                fos.write(data, 0, count);
            }
        }
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        Log.d("InputSave", "Finished saveFile attempt for route " + selectedRoute + (targetFile != null ? " at " + targetFile.getAbsolutePath() : ""));
    }
}