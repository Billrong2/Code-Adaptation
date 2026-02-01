@Override
public Bitmap getBitmap(String key) {
	DiskLruCache.Snapshot snapshot = null;
	InputStream in = null;
	Bitmap bitmap = null;
	try {
		snapshot = mDiskCache.get(key);
		if (snapshot == null) {
			return null;
		}
		in = new BufferedInputStream(snapshot.getInputStream(0), IO_BUFFER_SIZE);
		bitmap = BitmapFactory.decodeStream(in);
		if (BuildConfig.DEBUG) {
			Log.d("cache_test_DISK_", "image read from disk cache " + (bitmap != null ? key : ""));
		}
		return bitmap;
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	} finally {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException ignored) {
		}
		if (snapshot != null) {
			snapshot.close();
		}
	}
}