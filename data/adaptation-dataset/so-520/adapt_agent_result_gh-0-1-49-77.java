private Drawable fetchDrawable(final String urlString) {
        if (urlString == null || drawableMap == null) {
            return null;
        }

        if (drawableMap.containsKey(urlString)) {
            return drawableMap.get(urlString);
        }

        final String logTag = getClass().getSimpleName();
        Log.d(logTag, "image url:" + urlString);

        try (InputStream is = fetch(urlString)) {
            Drawable drawable = Drawable.createFromStream(is, "src");

            if (drawable != null) {
                drawableMap.put(urlString, drawable);
                Log.d(logTag,
                        "got a thumbnail drawable: " + drawable.getBounds() + ", "
                                + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                                + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
            } else {
                Log.w(logTag, "could not get thumbnail");
            }

            return drawable;
        } catch (MalformedURLException e) {
            Log.e(logTag, "fetchDrawable failed", e);
            return null;
        } catch (IOException e) {
            Log.e(logTag, "fetchDrawable failed", e);
            return null;
        }
    }