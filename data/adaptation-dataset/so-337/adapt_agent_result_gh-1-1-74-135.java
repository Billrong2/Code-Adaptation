public static List<StorageInfo> getStorageList() {
        List<StorageInfo> list = new ArrayList<StorageInfo>();
        String defPath = Environment.getExternalStorageDirectory().getPath();
        boolean defPathInternal = false;
        if (Build.VERSION.SDK_INT >= 9) {
            defPathInternal = !Environment.isExternalStorageRemovable();
        }
        String defPathState = Environment.getExternalStorageState();
        boolean defPathAvailable = Environment.MEDIA_MOUNTED.equals(defPathState)
                || Environment.MEDIA_MOUNTED_READ_ONLY.equals(defPathState);
        boolean defPathReadonly = Environment.MEDIA_MOUNTED_READ_ONLY.equals(defPathState);

        BufferedReader bufReader = null;
        try {
            HashSet<String> paths = new HashSet<String>();
            File mountsFile = new File("/proc/mounts");
            if (mountsFile.exists() && mountsFile.canRead()) {
                bufReader = new BufferedReader(new FileReader(mountsFile));
                String line;
                int curDisplayNumber = 1;
                Log.d(TAG, "/proc/mounts");
                while ((line = bufReader.readLine()) != null) {
                    Log.d(TAG, line);
                    if (line.contains("vfat") || line.contains("/mnt")) {
                        StringTokenizer tokens = new StringTokenizer(line, " ");
                        if (!tokens.hasMoreTokens()) {
                            continue;
                        }
                        tokens.nextToken(); // device
                        if (!tokens.hasMoreTokens()) {
                            continue;
                        }
                        String mountPoint = tokens.nextToken(); // mount point
                        if (paths.contains(mountPoint)) {
                            continue;
                        }
                        if (!tokens.hasMoreTokens()) {
                            continue;
                        }
                        tokens.nextToken(); // file system
                        if (!tokens.hasMoreTokens()) {
                            continue;
                        }
                        List<String> flags = Arrays.asList(tokens.nextToken().split(","));
                        boolean readonly = flags.contains("ro");

                        if (mountPoint.equals(defPath)) {
                            paths.add(defPath);
                            list.add(0, new StorageInfo(defPath, defPathInternal, readonly, -1));
                        } else if (line.contains("/dev/block/vold")) {
                            if (!line.contains("/mnt/secure")
                                    && !line.contains("/mnt/asec")
                                    && !line.contains("/mnt/obb")
                                    && !line.contains("/dev/mapper")
                                    && !line.contains("tmpfs")) {
                                paths.add(mountPoint);
                                list.add(new StorageInfo(mountPoint, false, readonly, curDisplayNumber++));
                            }
                        }
                    }
                }
            }

            if (!paths.contains(defPath) && defPathAvailable) {
                list.add(0, new StorageInfo(defPath, defPathInternal, defPathReadonly, -1));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } finally {
            if (bufReader != null) {
                try {
                    bufReader.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
        }
        return list;
    }