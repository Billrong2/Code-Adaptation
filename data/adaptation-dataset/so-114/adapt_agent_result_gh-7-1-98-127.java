@Override
public Bitmap getBitmap(String key) {
	DiskLruCache.Snapshot snapshot = null;
	BufferedInputStream in = null;
	Bitmap bitmap = null;
	try {
		snapshot = mDiskCache.get(key);
		if (snapshot == null) {
			return null;
		}
		InputStream raw = snapshot.getInputStream(0);
		if (raw == null) {
			return null;
		}
		in = new BufferedInputStream(raw, IO_BUFFER_SIZE);
		bitmap = BitmapFactory.decodeStream(in);
		return bitmap;
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	} finally {
		if (in != null) {
			try {
				in.close();
			} catch (IOException ignored) {
			}
		}
		if (snapshot != null) {
			snapshot.close();
		}
		if (LogA.isDebug()) {
			Log.d("cache_test_DISK_", bitmap != null ? "image read from disk " + key : "");
		}
	}
}