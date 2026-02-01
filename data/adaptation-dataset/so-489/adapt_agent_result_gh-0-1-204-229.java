public Bitmap getIconForComponent(Context context, String componentName, Bitmap defaultBitmap) {
        if(context == null || packageName == null) return defaultBitmap;

        // Lazy initialization of the icon pack
        if(!mLoaded) {
            try {
                load(context);
            } catch (Exception e) {
                // Gracefully fall back if loading fails
                return generateBitmap(componentName, defaultBitmap);
            }
        }

        // 1) Exact component mapping lookup
        try {
            if(componentName != null && mPackagesDrawables != null) {
                String mappedDrawable = mPackagesDrawables.get(componentName);
                if(mappedDrawable != null) {
                    Bitmap mappedBitmap = loadBitmap(context, mappedDrawable);
                    if(mappedBitmap != null)
                        return mappedBitmap;
                }
            }
        } catch (Exception e) {
            // Ignore and continue to fallbacks
        }

        // 2) Derived drawable name from component string
        try {
            if(componentName != null) {
                int start = componentName.indexOf('{') + 1;
                int end = componentName.indexOf('}', start);
                if(start > 0 && end > start) {
                    String derivedName = componentName
                            .substring(start, end)
                            .toLowerCase(Locale.getDefault())
                            .replace('.', '_')
                            .replace('/', '_');

                    Resources res = getResources(context);
                    if(res != null) {
                        int id = res.getIdentifier(derivedName, "drawable", packageName);
                        if(id > 0) {
                            Bitmap derivedBitmap = loadBitmap(context, derivedName);
                            if(derivedBitmap != null)
                                return derivedBitmap;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore and continue to fallback
        }

        // 3) Final fallback: generate bitmap using icon pack masks or default
        try {
            return generateBitmap(componentName, defaultBitmap);
        } catch (Exception e) {
            return defaultBitmap;
        }
    }