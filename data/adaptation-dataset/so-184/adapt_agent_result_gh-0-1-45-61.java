public static int stringToDimensionPixelSize(String dimension, DisplayMetrics metrics) {
        // Validate inputs
        if (dimension == null || dimension.trim().isEmpty()) {
            return 0;
        }
        if (metrics == null) {
            return 0;
        }

        // Ensure thread-safe cache
        if (!(cached instanceof java.util.concurrent.ConcurrentHashMap)) {
            cached = new java.util.concurrent.ConcurrentHashMap<String, Float>();
        }

        final String key = dimension;
        Float cachedValue = cached.get(key);
        float pixels;
        if (cachedValue != null) {
            pixels = cachedValue.floatValue();
        } else {
            try {
                InternalDimension internalDimension = stringToInternalDimension(dimension);
                pixels = TypedValue.applyDimension(internalDimension.unit, internalDimension.value, metrics);
                cached.put(key, pixels);
            } catch (RuntimeException e) {
                // Parsing error or unknown unit
                Log.e("DimensionConverter", "Failed to parse dimension: " + dimension, e);
                return 0;
            }
        }

        // Convert float pixels to int with rounding rules
        if (pixels == 0f) {
            return 0;
        }
        int rounded = (int) (pixels + (pixels > 0 ? 0.5f : -0.5f));
        if (rounded == 0) {
            return pixels > 0 ? 1 : -1;
        }
        return rounded;
    }