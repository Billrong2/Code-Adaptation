@Override
public void putBitmap(final String key, final Bitmap bitmap) {
    final String realKey = getRealKey(key);
    if (bitmap == null || realKey == null) {
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "putBitmap skipped due to null bitmap or key");
        }
        return;
    }

    DiskLruCache.Editor editor = null;
    try {
        editor = mDiskCache.edit(realKey);
        if (editor == null) {
            return;
        }

        if (writeBitmapToFile(bitmap, editor)) {
            editor.commit();
            mDiskCache.flush();
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "image put on disk cache " + realKey);
            }
        } else {
            editor.abort();
            if (BuildConfig.DEBUG) {
                Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + realKey);
            }
        }
    } catch (IOException | IllegalStateException e) {
        if (BuildConfig.DEBUG) {
            Log.d("cache_test_DISK_", "ERROR on: image put on disk cache " + realKey);
        }
        if (editor != null) {
            try {
                editor.abort();
            } catch (IOException ignored) {
            }
        }
    }
}