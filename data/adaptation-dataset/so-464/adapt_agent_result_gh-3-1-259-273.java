private int efficientTextSizeSearch(int startSize, int endSize, SizeTester sizeTester, RectF availableSpace) {
        // Compute and return the optimal text size using binary search and an optional cache
        if (!mInitialized || sizeTester == null || availableSpace == null) {
            return startSize;
        }
        if (startSize <= 0 || endSize <= 0 || startSize > endSize) {
            return Math.max(1, startSize);
        }

        final String text = getText();
        if (TextUtils.isEmpty(text)) {
            return startSize;
        }

        // Optional size cache keyed by text length
        if (mEnableSizeCache && mTextCachedSizes != null) {
            int key = text.length();
            synchronized (mTextCachedSizes) {
                int cached = mTextCachedSizes.get(key, -1);
                if (cached > 0) {
                    return cached;
                }
            }
        }

        int resultSize = startSize;
        try {
            resultSize = binarySearch(startSize, endSize, sizeTester, availableSpace);
        } catch (RuntimeException e) {
            // Fallback to minimum size on tester or search failure
            resultSize = startSize;
        }

        if (mEnableSizeCache && mTextCachedSizes != null) {
            int key = text.length();
            synchronized (mTextCachedSizes) {
                mTextCachedSizes.put(key, resultSize);
            }
        }

        return resultSize;
    }