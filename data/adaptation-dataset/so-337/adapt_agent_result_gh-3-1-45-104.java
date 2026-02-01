public static List<StorageInfo> getStorageList() {
    List<StorageInfo> list = new ArrayList<StorageInfo>();
    String def_path = Environment.getExternalStorageDirectory().getPath();
    boolean def_path_internal = !Environment.isExternalStorageRemovable();
    String def_path_state = Environment.getExternalStorageState();
    boolean def_path_available = Environment.MEDIA_MOUNTED.equals(def_path_state)
            || Environment.MEDIA_MOUNTED_READ_ONLY.equals(def_path_state);
    boolean def_path_readonly = Environment.MEDIA_MOUNTED_READ_ONLY.equals(def_path_state);

    HashSet<String> paths = new HashSet<String>();
    int cur_display_number = 1;

    try (BufferedReader buf_reader = new BufferedReader(new FileReader("/proc/mounts"))) {
        String line;
        while ((line = buf_reader.readLine()) != null) {
            if (line.length() == 0) {
                continue;
            }
            if (line.contains("vfat") || line.contains("/mnt")) {
                StringTokenizer tokens = new StringTokenizer(line, " ");
                if (tokens.countTokens() < 4) {
                    continue;
                }
                tokens.nextToken(); // device
                String mount_point = tokens.nextToken(); // mount point
                if (mount_point == null || paths.contains(mount_point)) {
                    continue;
                }
                tokens.nextToken(); // file system
                String flagsToken = tokens.nextToken();
                List<String> flags = Arrays.asList(flagsToken.split(","));
                boolean readonly = flags.contains("ro");

                if (mount_point.equals(def_path)) {
                    paths.add(def_path);
                    // default path found during parsing: display/index = 0, still inserted at head
                    list.add(0, new StorageInfo(def_path, def_path_internal, readonly, 0));
                } else if (line.contains("/dev/block/vold")) {
                    if (!line.contains("/mnt/secure")
                            && !line.contains("/mnt/asec")
                            && !line.contains("/mnt/obb")
                            && !line.contains("/dev/mapper")
                            && !line.contains("tmpfs")) {
                        paths.add(mount_point);
                        list.add(new StorageInfo(mount_point, false, readonly, cur_display_number++));
                    }
                }
            }
        }

        // fallback unchanged: if default path was not found but is available, add with display/index -1
        if (!paths.contains(def_path) && def_path_available) {
            list.add(0, new StorageInfo(def_path, def_path_internal, def_path_readonly, -1));
        }
    } catch (FileNotFoundException ex) {
        ex.printStackTrace();
    } catch (IOException ex) {
        ex.printStackTrace();
    }

    return list;
}