public Bitmap getIconForComponent(Context context, String componentName, Bitmap defaultBitmap) {
    if(context == null || defaultBitmap == null) return null;

    // Lazy-load appfilter data if needed (thread-safe)
    if(!mLoaded) {
        synchronized(this) {
            if(!mLoaded && !mIsLoading) {
                load(context);
            }
        }
    }

    Bitmap result = null;

    // 1) Try explicit mapping from appfilter
    if(componentName != null) {
        String mappedDrawable = mPackagesDrawables.get(componentName);
        if(mappedDrawable != null) {
            try {
                result = loadBitmap(context, mappedDrawable);
            } catch (Exception e) {
                result = null;
            }
        }
    }

    // 2) Heuristic fallback: derive drawable name from componentName
    if(result == null && componentName != null) {
        int start = componentName.indexOf("{") + 1;
        int end = componentName.indexOf("}", start);
        if(start > 0 && end > start) {
            String heuristicName = componentName
                    .substring(start, end)
                    .toLowerCase(java.util.Locale.getDefault())
                    .replace('.', '_')
                    .replace('/', '_');

            if(heuristicName.length() > 0) {
                try {
                    int id = getResources(context).getIdentifier(heuristicName, "drawable", packageName);
                    if(id > 0) {
                        result = loadBitmap(context, heuristicName);
                    }
                } catch (Exception e) {
                    result = null;
                }
            }
        }
    }

    // 3) Final fallback: generate themed bitmap from default
    if(result == null) {
        try {
            result = generateBitmap(componentName != null ? componentName : "", defaultBitmap);
        } catch (Exception e) {
            result = defaultBitmap;
        }
    }

    return result;
}