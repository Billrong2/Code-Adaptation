public void fetchDrawableAsync(final String urlString, final android.widget.ImageView target) {
        if (target == null || urlString == null || urlString.length() == 0) {
            return;
        }

        // Mark the ImageView with the requested URL to guard against recycling
        target.setTag(urlString);

        final android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());

        // If cached, immediately update on UI thread
        Drawable cached;
        synchronized (drawableMap) {
            cached = drawableMap.get(urlString);
        }
        if (cached != null) {
            final Drawable cachedDrawable = cached;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Object tag = target.getTag();
                    if (urlString.equals(tag)) {
                        target.setImageDrawable(cachedDrawable);
                    }
                }
            });
        }

        // TODO: hook for setting a placeholder drawable while loading

        // Always start background fetch
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                Drawable drawable = null;
                try {
                    is = fetch(urlString);
                    drawable = Drawable.createFromStream(is, "src");
                    if (drawable != null) {
                        synchronized (drawableMap) {
                            drawableMap.put(urlString, drawable);
                        }
                    }
                } catch (Exception e) {
                    // Suppress exceptions per async wrapper policy
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignore) {
                        }
                    }
                }

                final Drawable result = drawable;
                if (result != null) {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Object tag = target.getTag();
                            if (urlString.equals(tag)) {
                                target.setImageDrawable(result);
                            }
                        }
                    });
                }
            }
        }).start();
    }