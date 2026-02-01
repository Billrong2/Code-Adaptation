public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
    if (imageView == null || urlString == null || urlString.length() == 0) {
        return;
    }

    // Early cache usage
    final Drawable cached = drawableMap.get(urlString);
    if (cached != null) {
        imageView.setImageDrawable(cached);
        return;
    }

    // Tag ImageView to guard against recycling
    imageView.setTag(urlString);

    final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    new Thread(new Runnable() {
        @Override
        public void run() {
            InputStream is = null;
            try {
                is = fetch(urlString);
                final Drawable drawable = Drawable.createFromStream(is, "src");
                if (drawable != null) {
                    drawableMap.put(urlString, drawable);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Ensure this ImageView has not been reused for another URL
                            Object tag = imageView.getTag();
                            if (urlString.equals(tag)) {
                                imageView.setImageDrawable(drawable);
                            }
                        }
                    });
                }
            } catch (Exception ignored) {
                // Failure is silently ignored; placeholder (if any) remains
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }).start();
}