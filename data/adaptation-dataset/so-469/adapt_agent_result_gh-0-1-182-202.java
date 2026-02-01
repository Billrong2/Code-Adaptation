Drawable getDrawableIconForPackage(Context context, String componentName) {
        if(context == null || componentName == null || packageName == null) return null;

        // Lazy initialization
        if(!mLoaded) {
            load(context);
            if(!mLoaded) return null;
        }

        try {
            // 1) Exact component mapping
            String drawableName = mPackagesDrawables.get(componentName);
            if(drawableName != null) {
                Drawable d = loadDrawable(context, drawableName);
                if(d != null) return d;
            }

            // 2) Fallback: derive drawable name from componentName
            int start = componentName.indexOf("{") + 1;
            int end = componentName.indexOf("}", start);
            if(start > 0 && end > start) {
                String fallbackName = componentName
                        .substring(start, end)
                        .toLowerCase(Locale.getDefault())
                        .replace(".", "_")
                        .replace("/", "_");

                int id = getResources(context).getIdentifier(fallbackName, "drawable", packageName);
                if(id > 0) {
                    return loadDrawable(context, fallbackName);
                }
            }
        } catch (Resources.NotFoundException e) {
            return null;
        } catch (RuntimeException e) {
            return null;
        }

        return null;
    }