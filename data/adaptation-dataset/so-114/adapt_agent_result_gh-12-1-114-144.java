public Bitmap getBitmap(String url) {
        DiskLruCache.Snapshot snapshot = null;
        InputStream in = null;
        try {
            if (url == null) {
                return null;
            }
            snapshot = mDiskCache.get(CacheUtil.md5(url));
            if (snapshot == null) {
                return null;
            }
            in = snapshot.getInputStream(0);
            if (in == null) {
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(in, IO_BUFFER_SIZE);
            synchronized (sDecodeLock) {
                try {
                    return BitmapFactory.decodeStream(bufferedInputStream);
                } catch (OutOfMemoryError oom) {
                    return null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (snapshot != null) {
                snapshot.close();
            }
        }
    }