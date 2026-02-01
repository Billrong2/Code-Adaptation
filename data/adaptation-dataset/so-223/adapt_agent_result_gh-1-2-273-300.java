@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public static Bitmap blur(Context context, Bitmap image, int targetWidth, int targetHeight, float blurRadius) {
    if (context == null || image == null) {
        return null;
    }
    if (targetWidth <= 0 || targetHeight <= 0) {
        return null;
    }
    // RenderScript blur radius must be > 0 and <= 25
    if (blurRadius <= 0f) {
        return null;
    }
    if (blurRadius > 25f) {
        blurRadius = 25f;
    }

    Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, targetWidth, targetHeight, false);
    Bitmap outputBitmap = Bitmap.createBitmap(scaledBitmap);

    RenderScript rs = null;
    Allocation inAllocation = null;
    Allocation outAllocation = null;
    ScriptIntrinsicBlur blurScript = null;

    try {
        rs = RenderScript.create(context);
        blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        inAllocation = Allocation.createFromBitmap(rs, scaledBitmap);
        outAllocation = Allocation.createFromBitmap(rs, outputBitmap);

        blurScript.setRadius(blurRadius);
        blurScript.setInput(inAllocation);
        blurScript.forEach(outAllocation);
        outAllocation.copyTo(outputBitmap);

        return outputBitmap;
    } finally {
        if (blurScript != null) {
            blurScript.destroy();
        }
        if (inAllocation != null) {
            inAllocation.destroy();
        }
        if (outAllocation != null) {
            outAllocation.destroy();
        }
        if (rs != null) {
            rs.destroy();
        }
    }
}