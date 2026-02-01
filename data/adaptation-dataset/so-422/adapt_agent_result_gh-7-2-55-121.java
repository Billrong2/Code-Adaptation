public static List<StorageInfo> getStorageList() {
		final List<StorageInfo> list = new ArrayList<StorageInfo>();

		final String defaultPath = Environment.getExternalStorageDirectory().getPath();
		final boolean defaultPathRemovable = Environment.isExternalStorageRemovable();
		final String defaultPathState = Environment.getExternalStorageState();
		final boolean defaultPathAvailable = Environment.MEDIA_MOUNTED.equals(defaultPathState)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(defaultPathState);
		final boolean defaultPathReadonly = Environment.MEDIA_MOUNTED_READ_ONLY.equals(defaultPathState);

		final HashSet<String> paths = new HashSet<String>();
		int removableIndex = 1;

		if (defaultPathAvailable) {
			paths.add(defaultPath);
			list.add(new StorageInfo(defaultPath, defaultPathReadonly, defaultPathRemovable,
					defaultPathRemovable ? removableIndex++ : -1));
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/proc/mounts"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("vfat") || line.contains("/mnt")) {
					StringTokenizer tokens = new StringTokenizer(line, " ");
					tokens.nextToken(); // device
					String mountPoint = tokens.nextToken(); // mount point
					if (paths.contains(mountPoint)) {
						continue;
					}
					tokens.nextToken(); // file system
					List<String> flags = Arrays.asList(tokens.nextToken().split(","));
					boolean readonly = flags.contains("ro");

					if (line.contains("/dev/block/vold")) {
						if (!line.contains("/mnt/secure")
								&& !line.contains("/mnt/asec")
								&& !line.contains("/mnt/obb")
								&& !line.contains("/dev/mapper")
								&& !line.contains("tmpfs")) {
							paths.add(mountPoint);
							list.add(new StorageInfo(mountPoint, readonly, true, removableIndex++));
						}
					}
				}
			}
		} catch (FileNotFoundException ex) {
			Log.e(TAG, "Unable to read /proc/mounts", ex);
		} catch (IOException ex) {
			Log.e(TAG, "Error while reading /proc/mounts", ex);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
					Log.e(TAG, "Error closing /proc/mounts reader", ex);
				}
			}
		}
		return list;
	}
