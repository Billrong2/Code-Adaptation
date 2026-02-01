public void fetchDrawableAsync(final DrawableProducer dp, final ImageView imageView) {
    if (dp == null || imageView == null) {
        return;
    }

    final String key = dp.getKey();
    if (key == null) {
        return;
    }

    // Check strong-reference cache first
    final Drawable cached = drawableMap.get(key);
    if (cached != null) {
        imageView.setImageDrawable(cached);
        return;
    }

    // Optional placeholder handling (producer may decide what to return)
    final Drawable placeholder = dp.getPlaceholderDrawable();
    if (placeholder != null) {
        imageView.setImageDrawable(placeholder);
    }

    final android.os.Handler uiHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    // Run fetch off the UI thread
    new java.lang.Thread(new java.lang.Runnable() {
        @Override
        public void run() {
            final Drawable fetched = fetchDrawable(dp);
            if (fetched == null) {
                return;
            }

            // Cache result with strong reference
            drawableMap.put(key, fetched);

            // Post result back to UI thread
            uiHandler.post(new java.lang.Runnable() {
                @Override
                public void run() {
                    if (imageView != null) {
                        imageView.setImageDrawable(fetched);
                    }
                }
            );
        }
    }).start();
}