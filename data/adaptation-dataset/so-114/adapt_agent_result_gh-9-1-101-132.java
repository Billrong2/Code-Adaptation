@Override
public Bitmap getBitmap(String key) {
    key = getRealKey(key);
    DiskLruCache.Snapshot snapshot = null;
    try {
        snapshot = mDiskCache.get(key);
        if (snapshot == null) {
            return null;
        }
        InputStream in = new BufferedInputStream(snapshot.getInputStream(0), IO_BUFFER_SIZE);
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        if (bitmap != null) {
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "image read from disk cache " + key);
            }
        }
        return bitmap;
    } catch (IOException e) {
        e.printStackTrace();
        return null;
    } finally {
        if (snapshot != null) {
            snapshot.close();
        }
    }
}