public static List<StorageInfo> getStorageList() {
	List<StorageInfo> list = new ArrayList<StorageInfo>();
	String def_path = Environment.getExternalStorageDirectory() != null
			? Environment.getExternalStorageDirectory().getPath()
			: null;
	String def_state = Environment.getExternalStorageState();
	boolean def_path_available = Environment.MEDIA_MOUNTED.equals(def_state)
			|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(def_state);
	boolean def_path_readonly = Environment.MEDIA_MOUNTED_READ_ONLY.equals(def_state);
	boolean def_path_removable = Environment.isExternalStorageRemovable();

	HashSet<String> paths = new HashSet<String>();
	int cur_removable_number = 1;

	try (BufferedReader bufReader = new BufferedReader(new FileReader("/proc/mounts"))) {
		String line;
		while ((line = bufReader.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}
			if (!line.contains("/dev/block/vold")) {
				continue;
			}
			if (line.contains("/mnt/secure")
					|| line.contains("/mnt/asec")
					|| line.contains("/mnt/obb")
					|| line.contains("/dev/mapper")
					|| line.contains("tmpfs")) {
				continue;
			}

			StringTokenizer tokens = new StringTokenizer(line, " ");
			if (tokens.countTokens() < 4) {
				continue;
			}
			tokens.nextToken(); // device
			String mountPoint = tokens.nextToken(); // mount point
			if (mountPoint == null || mountPoint.length() == 0) {
				continue;
			}
			if (paths.contains(mountPoint)) {
				continue;
			}
			tokens.nextToken(); // filesystem
			String flagsToken = tokens.nextToken();
			boolean readonly = flagsToken != null && Arrays.asList(flagsToken.split(",")).contains("ro");

			paths.add(mountPoint);
			list.add(new StorageInfo(mountPoint, readonly, true, cur_removable_number++));
		}
	} catch (FileNotFoundException e) {
		// ignore: /proc/mounts not available
	} catch (IOException e) {
		// ignore I/O errors while reading mounts
	}

	if (def_path != null && def_path_available && !paths.contains(def_path)) {
		int def_number = def_path_removable ? cur_removable_number++ : -1;
		list.add(0, new StorageInfo(def_path, def_path_readonly, def_path_removable, def_number));
	}

	return list;
}