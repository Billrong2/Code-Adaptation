private static List<String> getSourcePaths(Context context) throws PackageManager.NameNotFoundException, IOException {
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        final PackageManager packageManager = context.getPackageManager();
        final ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        if (applicationInfo == null) {
            throw new NullPointerException("applicationInfo == null");
        }

        final File sourceApk = new File(applicationInfo.sourceDir);
        final File dexDir = new File(applicationInfo.dataDir, SECONDARY_FOLDER_NAME);

        final List<String> sourcePaths = new ArrayList<>();
        sourcePaths.add(applicationInfo.sourceDir); // add the default apk path

        // the prefix of extracted file, ie: test.classes
        final String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;
        // the total dex numbers
        final int totalDexNumber = getMultiDexPreferences(context).getInt(KEY_DEX_NUMBER, 1);

        if (totalDexNumber > 1 && !dexDir.isDirectory()) {
            throw new IOException("Missing secondary dex directory '" + dexDir.getPath() + "'");
        }

        for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            // for each dex file, ie: test.classes2.zip, test.classes3.zip...
            final String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
            final File extractedFile = new File(dexDir, fileName);
            if (extractedFile.isFile()) {
                sourcePaths.add(extractedFile.getAbsolutePath());
                // we ignore the verify zip part
            } else {
                throw new IOException("Missing extracted secondary dex file '" + extractedFile.getPath() + "'");
            }
        }

        return sourcePaths;
    }