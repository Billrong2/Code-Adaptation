public static Map<String, File> getAllStorageLocations() {
    final Map<String, File> map = new HashMap<String, File>(10);

    final List<String> mMounts = new ArrayList<String>(10);
    final List<String> mVold = new ArrayList<String>(10);
    mMounts.add("/mnt/sdcard");
    mVold.add("/mnt/sdcard");

    // Parse /proc/mounts
    try {
        final File mountFile = new File("/proc/mounts");
        if (mountFile.exists()) {
            try (Scanner scanner = new Scanner(mountFile)) {
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    if (line.startsWith("/dev/block/vold/")) {
                        final String[] lineElements = line.split(" ");
                        if (lineElements.length > 1) {
                            final String element = lineElements[1];
                            if (!"/mnt/sdcard".equals(element)) {
                                mMounts.add(element);
                            }
                        }
                    }
                }
            }
        }
    } catch (SecurityException e) {
        Log.w(TAG, "Unable to read /proc/mounts", e);
    }

    // Parse vold.fstab
    try {
        final File voldFile = new File("/system/etc/vold.fstab");
        if (voldFile.exists()) {
            try (Scanner scanner = new Scanner(voldFile)) {
                while (scanner.hasNextLine()) {
                    final String line = scanner.nextLine();
                    if (line.startsWith("dev_mount")) {
                        final String[] lineElements = line.split(" ");
                        if (lineElements.length > 2) {
                            String element = lineElements[2];
                            final int idx = element.indexOf(":");
                            if (idx > 0) {
                                element = element.substring(0, idx);
                            }
                            if (!"/mnt/sdcard".equals(element)) {
                                mVold.add(element);
                            }
                        }
                    }
                }
            }
        }
    } catch (SecurityException e) {
        Log.w(TAG, "Unable to read vold.fstab", e);
    }

    // Keep only mounts present in vold
    for (int i = 0; i < mMounts.size(); i++) {
        final String mount = mMounts.get(i);
        if (!mVold.contains(mount)) {
            mMounts.remove(i--);
        }
    }
    mVold.clear();

    // Deduplicate by content hash (as per original logic)
    final List<String> mountHash = new ArrayList<String>(10);
    for (String mount : mMounts) {
        final File root = new File(mount);
        if (root.exists() && root.isDirectory() && root.canWrite()) {
            final File[] list = root.listFiles();
            final StringBuilder hashBuilder = new StringBuilder("[");
            if (list != null) {
                for (File f : list) {
                    hashBuilder.append(f.getName().hashCode()).append(':').append(f.length()).append(", ");
                }
            }
            hashBuilder.append(']');
            final String hash = hashBuilder.toString();
            if (!mountHash.contains(hash)) {
                String key = SD_CARD + "_" + map.size();
                if (map.size() == 0) {
                    key = SD_CARD;
                } else if (map.size() == 1) {
                    key = EXTERNAL_SD_CARD;
                }
                mountHash.add(hash);
                map.put(key, root);
            }
        }
    }
    mMounts.clear();

    // Always include the standard external storage directory
    try {
        final File defaultExt = Environment.getExternalStorageDirectory();
        if (defaultExt != null && defaultExt.exists() && defaultExt.isDirectory()) {
            boolean exists = false;
            for (File f : map.values()) {
                if (defaultExt.equals(f)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                map.put(map.isEmpty() ? SD_CARD : (map.containsKey(EXTERNAL_SD_CARD) ? SD_CARD + "_" + map.size() : EXTERNAL_SD_CARD), defaultExt);
            }
        }
    } catch (SecurityException e) {
        Log.w(TAG, "Unable to access default external storage", e);
    }

    // Augment with environment variables
    try {
        final String envExternal = System.getenv("EXTERNAL_STORAGE");
        if (envExternal != null) {
            final File f = new File(envExternal);
            if (f.exists() && f.isDirectory() && f.canWrite()) {
                boolean exists = false;
                for (File existing : map.values()) {
                    if (existing.equals(f)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    map.put(SD_CARD + "_" + map.size(), f);
                }
            }
        }

        final String envSecondary = System.getenv("SECONDARY_STORAGE");
        if (envSecondary != null) {
            final String[] paths = envSecondary.split(File.pathSeparator);
            for (String p : paths) {
                if (p == null || p.length() == 0) continue;
                final File f = new File(p);
                if (f.exists() && f.isDirectory() && f.canWrite()) {
                    boolean exists = false;
                    for (File existing : map.values()) {
                        if (existing.equals(f)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        map.put(SD_CARD + "_" + map.size(), f);
                    }
                }
            }
        }
    } catch (SecurityException e) {
        Log.w(TAG, "Unable to read storage environment variables", e);
    }

    return map;
}