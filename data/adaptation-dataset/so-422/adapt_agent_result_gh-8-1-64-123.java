public static List<StorageInfo> getStorageList() {
    final List<StorageInfo> list = new ArrayList<StorageInfo>();

    final String defPath = Environment.getExternalStorageDirectory().getPath();
    final boolean defPathRemovable = Environment.isExternalStorageRemovable();
    final String defPathState = Environment.getExternalStorageState();
    final boolean defPathAvailable = defPathState.equals(Environment.MEDIA_MOUNTED)
            || defPathState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    final boolean defPathReadonly = Environment.getExternalStorageState()
            .equals(Environment.MEDIA_MOUNTED_READ_ONLY);

    final HashSet<String> paths = new HashSet<String>();
    int currentRemovableNumber = 1;

    if (defPathAvailable) {
        paths.add(defPath);
        list.add(0, new StorageInfo(
                defPath,
                defPathReadonly,
                defPathRemovable,
                defPathRemovable ? currentRemovableNumber++ : -1
        ));
    }

    try (BufferedReader reader = new BufferedReader(new FileReader("/proc/mounts"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("vfat") || line.contains("/mnt")) {
                StringTokenizer tokens = new StringTokenizer(line, " ");
                tokens.nextToken(); // device (unused)
                String mountPoint = tokens.nextToken(); // mount point

                if (paths.contains(mountPoint)) {
                    continue;
                }

                tokens.nextToken(); // file system (unused)
                List<String> flags = Arrays.asList(tokens.nextToken().split(","));
                boolean readonly = flags.contains("ro");

                if (line.contains("/dev/block/vold")) {
                    if (!line.contains("/mnt/secure")
                            && !line.contains("/mnt/asec")
                            && !line.contains("/mnt/obb")
                            && !line.contains("/dev/mapper")
                            && !line.contains("tmpfs")) {
                        paths.add(mountPoint);
                        list.add(new StorageInfo(mountPoint, readonly, true, currentRemovableNumber++));
                    }
                }
            }
        }
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    return list;
}