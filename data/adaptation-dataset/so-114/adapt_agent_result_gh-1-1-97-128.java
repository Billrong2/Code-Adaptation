@Override
public Bitmap getBitmap(String key) {

	key = createKey(key);

	DiskLruCache.Snapshot snapshot = null;
	InputStream inputStream = null;
	BufferedInputStream bufferedInputStream = null;
	Bitmap bitmap = null;

	try {
		snapshot = mDiskCache.get(key);
		if (snapshot == null) {
			return null;
		}

		inputStream = snapshot.getInputStream(0);
		if (inputStream == null) {
			return null;
		}

		bufferedInputStream = new BufferedInputStream(inputStream, IO_BUFFER_SIZE);
		bitmap = BitmapFactory.decodeStream(bufferedInputStream);
		return bitmap;

	} catch (IOException e) {
		e.printStackTrace();
		return null;
	} finally {
		try {
			if (bufferedInputStream != null) {
				bufferedInputStream.close();
			}
		} catch (IOException ignored) {
		}
		try {
			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException ignored) {
		}
		if (snapshot != null) {
			snapshot.close();
		}
	}
}