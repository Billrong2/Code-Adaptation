private Drawable fetchDrawable(final String urlString) {
    if (urlString == null) {
        Log.w(getClass().getSimpleName(), "fetchDrawable called with null url");
        return null;
    }

    if (drawableMap.containsKey(urlString)) {
        return drawableMap.get(urlString);
    }

    Log.d(getClass().getSimpleName(), "image url:" + urlString);
    try (InputStream is = fetch(urlString)) {
        final Drawable drawable = Drawable.createFromStream(is, "src");
        if (drawable == null) {
            Log.w(getClass().getSimpleName(), "Unable to create drawable from stream for url: " + urlString);
            return null;
        }
        drawableMap.put(urlString, drawable);
        Log.d(getClass().getSimpleName(), "cached thumbnail drawable (strong ref): "
                + drawable.getBounds() + ", "
                + drawable.getIntrinsicHeight() + "," + drawable.getIntrinsicWidth() + ", "
                + drawable.getMinimumHeight() + "," + drawable.getMinimumWidth());
        return drawable;
    } catch (MalformedURLException e) {
        Log.e(getClass().getSimpleName(), "fetchDrawable failed (bad url): " + urlString, e);
        return null;
    } catch (IOException e) {
        Log.e(getClass().getSimpleName(), "fetchDrawable failed (io error): " + urlString, e);
        return null;
    }
}