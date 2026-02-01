public static Bitmap blur(Context ctx, Bitmap sourceBitmap, float radius) {
        if (ctx == null || sourceBitmap == null) return sourceBitmap;

        // Clamp radius to RenderScript supported range (0 < r <= 25)
        float safeRadius = radius;
        if (safeRadius <= 0f) safeRadius = 0.1f;
        if (safeRadius > 25f) safeRadius = 25f;

        android.support.v8.renderscript.RenderScript rs = null;
        android.support.v8.renderscript.Allocation tmpIn = null;
        android.support.v8.renderscript.Allocation tmpOut = null;
        try {
            // Operate at full resolution; output size matches input size
            Bitmap outputBitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);

            rs = android.support.v8.renderscript.RenderScript.create(ctx);
            android.support.v8.renderscript.ScriptIntrinsicBlur blurScript =
                    android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));

            tmpIn = android.support.v8.renderscript.Allocation.createFromBitmap(rs, sourceBitmap);
            tmpOut = android.support.v8.renderscript.Allocation.createFromBitmap(rs, outputBitmap);

            blurScript.setRadius(safeRadius);
            blurScript.setInput(tmpIn);
            blurScript.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        } catch (android.support.v8.renderscript.RSIllegalArgumentException e) {
            // Invalid radius or RenderScript state; fall back to original bitmap
            return sourceBitmap;
        } catch (IllegalArgumentException e) {
            // Bitmap/allocation creation failure; fall back safely
            return sourceBitmap;
        } finally {
            if (tmpIn != null) tmpIn.destroy();
            if (tmpOut != null) tmpOut.destroy();
            if (rs != null) rs.destroy();
        }
    }