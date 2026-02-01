public static java.util.List<String> getAllClasses() throws java.io.IOException {
        final java.util.List<String> sourcePaths;
        try {
            sourcePaths = getSourcePaths();
        } catch (Exception e) {
            throw new java.io.IOException("Failed to obtain source dex paths", e);
        }

        if (sourcePaths == null || sourcePaths.isEmpty()) {
            return new java.util.ArrayList<>();
        }

        final java.util.Set<String> classNames = new java.util.HashSet<>();

        for (final String path : sourcePaths) {
            if (path == null || path.length() == 0) {
                continue;
            }

            final java.io.File dexFileOnDisk = new java.io.File(path);
            if (!dexFileOnDisk.exists() || !dexFileOnDisk.canRead()) {
                continue;
            }

            dalvik.system.DexFile dexFile = null;
            try {
                // Use loadDex for extracted secondary dex zip files to avoid dalvik-cache permission issues
                if (path.endsWith(EXTRACTED_SUFFIX)) {
                    final String optimizedPath = path + ".dex";
                    dexFile = dalvik.system.DexFile.loadDex(path, optimizedPath, 0);
                } else {
                    // Primary APK or direct dex
                    dexFile = new dalvik.system.DexFile(path);
                }

                final java.util.Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    final String className = entries.nextElement();
                    if (className != null) {
                        classNames.add(className);
                    }
                }
            } catch (java.io.IOException | RuntimeException e) {
                throw new java.io.IOException("Failed to enumerate classes from dex path: " + path, e);
            } finally {
                if (dexFile != null) {
                    try {
                        dexFile.close();
                    } catch (java.io.IOException ignored) {
                        // ignore close failures
                    }
                }
            }
        }

        return new java.util.ArrayList<>(classNames);
    }