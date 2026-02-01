public Bitmap getBitmap(final String key) {
        if (diskCache == null || key == null) {
            return null;
        }

        com.jakewharton.disklrucache.DiskLruCache.Snapshot snapshot = null;
        java.io.InputStream inputStream = null;
        java.io.BufferedInputStream bufferedInputStream = null;
        try {
            snapshot = diskCache.get(key);
            if (snapshot == null) {
                return null;
            }

            inputStream = snapshot.getInputStream(0);
            if (inputStream == null) {
                return null;
            }

            bufferedInputStream = new java.io.BufferedInputStream(inputStream, com.felipecsl.android.Utils.IO_BUFFER_SIZE);
            return android.graphics.BitmapFactory.decodeStream(bufferedInputStream);
        } catch (final java.io.IOException e) {
            android.util.Log.e(TAG, "ERROR reading image from disk cache " + key, e);
            return null;
        } finally {
            try {
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
            } catch (final java.io.IOException ignored) {}
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (final java.io.IOException ignored) {}
            if (snapshot != null) {
                snapshot.close();
            }
        }
    }