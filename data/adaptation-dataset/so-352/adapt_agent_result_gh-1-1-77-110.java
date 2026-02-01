private static List<String> getAllClasses(Context context) throws PackageManager.NameNotFoundException, IOException {
        if (context == null) {
            throw new IOException("Context is null");
        }
        List<String> dexPaths = getSourcePaths(context);
        if (dexPaths == null) {
            throw new IOException("Dex path list is null");
        }
        java.util.LinkedHashSet<String> classNameSet = new java.util.LinkedHashSet<>();
        for (String dexPath : dexPaths) {
            if (dexPath == null) {
                continue;
            }
            File dexFileOnDisk = new File(dexPath);
            if (!dexFileOnDisk.isFile() || !dexFileOnDisk.canRead()) {
                throw new IOException("Dex path not readable: " + dexPath);
            }
            DexFile dexFile = null;
            File optimizedOutput = null;
            try {
                // Secondary dex archives are zip files and should be loaded with an optimized output
                if (dexPath.endsWith(EXTRACTED_SUFFIX)) {
                    File cacheDir = context.getCacheDir();
                    if (cacheDir == null) {
                        throw new IOException("Cache directory is null, cannot create optimized dex output");
                    }
                    optimizedOutput = File.createTempFile("opt_dex_", ".dex", cacheDir);
                    dexFile = DexFile.loadDex(dexPath, optimizedOutput.getAbsolutePath(), 0);
                } else {
                    // Primary APK dex can be opened directly
                    dexFile = new DexFile(dexPath);
                }
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (className != null) {
                        classNameSet.add(className);
                    }
                }
            } catch (IOException | RuntimeException e) {
                throw new IOException("Failed to load dex file '" + dexPath + "'", e);
            } finally {
                if (dexFile != null) {
                    try {
                        dexFile.close();
                    } catch (IOException ignored) {
                        // ignore close failure
                    }
                }
                if (optimizedOutput != null && optimizedOutput.exists()) {
                    // best-effort cleanup of temporary optimized dex file
                    optimizedOutput.delete();
                }
            }
        }
        return new ArrayList<>(classNameSet);
    }