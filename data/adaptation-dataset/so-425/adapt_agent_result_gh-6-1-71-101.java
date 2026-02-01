@SuppressWarnings("unused")
public void put(final String key, final Bitmap data) {
    if (diskCache == null) {
        return;
    }

    DiskLruCache.Editor editor = null;
    try {
        editor = diskCache.edit(key);
        if (editor == null) {
            return;
        }

        if (writeBitmapToFile(data, editor)) {
            editor.commit();
            if (BuildConfig.DEBUG && ImageManager.LOG_CACHE_OPERATIONS) {
                Log.v(TAG, "image put on disk cache " + key);
            }
        } else {
            try {
                editor.abort();
            } catch (final IOException ignored) {
            } catch (final IllegalStateException ignored) {
            }
            Log.e(TAG, "ERROR putting image on disk cache " + key);
        }
    } catch (final IOException e) {
        Log.e(TAG, "ERROR putting image on disk cache " + key, e);
        if (editor != null) {
            try {
                editor.abort();
            } catch (final IOException ignored) {
            } catch (final IllegalStateException ignored) {
            }
        }
    }
}