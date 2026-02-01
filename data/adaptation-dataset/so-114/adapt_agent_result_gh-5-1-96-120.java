public Bitmap getBitmap(String key) {

        if (mDiskCache == null || key == null) {
            return null;
        }

        com.jakewharton.DiskLruCache.Snapshot snapshot = null;
        java.io.InputStream in = null;
        try {
            snapshot = mDiskCache.get(key);
            if (snapshot == null) {
                return null;
            }

            in = new java.io.BufferedInputStream(snapshot.getInputStream(0));
            return android.graphics.BitmapFactory.decodeStream(in);
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (java.io.IOException ignored) {
                }
            }
            if (snapshot != null) {
                snapshot.close();
            }
        }

    }