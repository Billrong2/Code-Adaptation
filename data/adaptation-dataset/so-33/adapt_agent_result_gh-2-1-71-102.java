public void loadDrawableAsync(final DrawableProducer dp, final ImageView imageView) {
    if (dp == null || imageView == null) {
        return;
    }
    final String key = dp.getKey();
    if (key == null) {
        return;
    }

    // Tag ImageView with the key to avoid applying results to a recycled view
    imageView.setTag(key);

    // Cache hit: apply immediately on UI thread
    Drawable cached = drawableMap.get(key);
    if (cached != null) {
        imageView.setImageDrawable(cached);
        return;
    }

    // Optional placeholder while loading (clearing is safest without a known placeholder)
    imageView.setImageDrawable(null);

    final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

    new Thread(new Runnable() {
        @Override
        public void run() {
            // Delegate network/creation and exception handling to the producer
            final Drawable fetched = dp.getDrawable();
            if (fetched == null) {
                return;
            }
            drawableMap.put(key, fetched);

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Object tag = imageView.getTag();
                    if (tag != null && key.equals(tag)) {
                        imageView.setImageDrawable(fetched);
                    }
                }
            });
        }
    }).start();
}