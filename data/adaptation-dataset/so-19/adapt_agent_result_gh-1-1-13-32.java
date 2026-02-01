public static int computeSquareMeasureSpec(final android.view.ViewGroup parent, final int widthMeasureSpec, final int heightMeasureSpec) {
    // Validate (unused) parent for API contract clarity
    if (parent == null) {
        // No parent context; proceed with safe defaults
    }

    final int widthMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
    final int widthSize = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
    final int heightMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
    final int heightSize = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

    int size;

    if (widthMode == android.view.View.MeasureSpec.EXACTLY && widthSize > 0) {
        size = widthSize;
    } else if (heightMode == android.view.View.MeasureSpec.EXACTLY && heightSize > 0) {
        size = heightSize;
    } else {
        final int safeWidth = widthMode == android.view.View.MeasureSpec.UNSPECIFIED ? 0 : Math.max(0, widthSize);
        final int safeHeight = heightMode == android.view.View.MeasureSpec.UNSPECIFIED ? 0 : Math.max(0, heightSize);
        size = safeWidth < safeHeight ? safeWidth : safeHeight;
    }

    if (size < 0) {
        size = 0;
    }

    // Always return an EXACTLY square MeasureSpec; callers must apply it to both width and height
    return android.view.View.MeasureSpec.makeMeasureSpec(size, android.view.View.MeasureSpec.EXACTLY);
}