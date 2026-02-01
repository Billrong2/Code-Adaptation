    /**
     * Blur a bitmap using RenderScript.
     *
     * @param context     valid Context used to create the RenderScript instance
     * @param src         source bitmap to blur (must be non-null and not recycled)
     * @param outWidth    desired output bitmap width (must be > 0)
     * @param outHeight   desired output bitmap height (must be > 0)
     * @param blurRadius  blur radius in the range (0, 25]
     * @return blurred bitmap, or null if input is invalid or blur fails
     */
    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blur(android.content.Context context, Bitmap src, int outWidth, int outHeight, float blurRadius) {
        if (context == null || src == null || src.isRecycled()) {
            return null;
        }
        if (outWidth <= 0 || outHeight <= 0) {
            return null;
        }
        if (blurRadius <= 0f || blurRadius > 25f) {
            return null;
        }

        android.renderscript.RenderScript renderScript = null;
        try {
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(src, outWidth, outHeight, false);
            Bitmap outputBitmap = Bitmap.createBitmap(scaledBitmap);

            renderScript = android.renderscript.RenderScript.create(context);
            android.renderscript.ScriptIntrinsicBlur blurScript = android.renderscript.ScriptIntrinsicBlur.create(
                    renderScript, android.renderscript.Element.U8_4(renderScript));

            android.renderscript.Allocation inputAllocation = android.renderscript.Allocation.createFromBitmap(renderScript, scaledBitmap);
            android.renderscript.Allocation outputAllocation = android.renderscript.Allocation.createFromBitmap(renderScript, outputBitmap);

            blurScript.setRadius(blurRadius);
            blurScript.setInput(inputAllocation);
            blurScript.forEach(outputAllocation);
            outputAllocation.copyTo(outputBitmap);

            return outputBitmap;
        } catch (android.renderscript.RSRuntimeException e) {
            com.iwillow.android.lib.log.Log.e("BitmapUtil", "RenderScript blur failed", e);
            return null;
        } finally {
            if (renderScript != null) {
                renderScript.destroy();
            }
        }
    }