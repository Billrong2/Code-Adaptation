public static List<String> getSourcePaths() throws PackageManager.NameNotFoundException {
    List<String> sourcePaths = new ArrayList<>();

    // Resolve application info via application-level accessors
    PackageManager pm = getPackageManager();
    String packageName = getPackageName();
    if (pm == null || packageName == null) {
        return sourcePaths;
    }

    ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
    if (applicationInfo == null || applicationInfo.sourceDir == null) {
        return sourcePaths;
    }

    // Always include base APK path first
    sourcePaths.add(applicationInfo.sourceDir);

    // Prepare secondary dex directory
    File dataDir = applicationInfo.dataDir != null ? new File(applicationInfo.dataDir) : null;
    File dexDir = dataDir != null ? new File(dataDir, SECONDARY_FOLDER_NAME) : null;

    // the prefix of extracted file, ie: app.apk.classes
    File sourceApk = new File(applicationInfo.sourceDir);
    String extractedFilePrefix = sourceApk.getName() + EXTRACTED_NAME_EXT;

    // the total dex numbers (be tolerant of gaps)
    int totalDexNumber = 1;
    try {
        SharedPreferences prefs = getMultiDexPreferences();
        if (prefs != null) {
            totalDexNumber = prefs.getInt(KEY_DEX_NUMBER, 1);
        }
    } catch (Throwable ignored) {
        // ignore preference access issues
    }

    if (dexDir != null && dexDir.exists() && dexDir.isDirectory()) {
        for (int secondaryNumber = 2; secondaryNumber <= totalDexNumber; secondaryNumber++) {
            String fileName = extractedFilePrefix + secondaryNumber + EXTRACTED_SUFFIX;
            File extractedFile = new File(dexDir, fileName);
            if (extractedFile.isFile() && extractedFile.canRead()) {
                sourcePaths.add(extractedFile.getAbsolutePath());
            }
            // missing secondary dex files are skipped intentionally
        }
    }

    // Instant Run support: append all dex/jar files under instant-run dex directory
    if (dataDir != null) {
        File instantRunDexDir = new File(dataDir, INSTANT_RUN_DEX_DIR_PATH);
        if (instantRunDexDir.exists() && instantRunDexDir.isDirectory()) {
            File[] files = instantRunDexDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    try {
                        if (f != null && f.isFile() && f.canRead()) {
                            String name = f.getName();
                            if (name.endsWith(".dex") || name.endsWith(".jar") || name.endsWith(EXTRACTED_SUFFIX)) {
                                sourcePaths.add(f.getAbsolutePath());
                            }
                        }
                    } catch (SecurityException ignored) {
                        // ignore unreadable files
                    }
                }
            }
        }
    }

    return sourcePaths;
}