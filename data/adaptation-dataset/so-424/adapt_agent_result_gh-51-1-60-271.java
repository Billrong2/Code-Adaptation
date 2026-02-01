public static Bitmap fastBlurPreserveAlpha(Bitmap src, int outWidth, int outHeight, int radius) {
    if (src == null || src.isRecycled()) {
        return null;
    }
    if (outWidth <= 0 || outHeight <= 0 || radius < 1) {
        return src;
    }

    Bitmap.Config config = src.getConfig();
    if (config == null) {
        config = Bitmap.Config.ARGB_8888;
    }

    Bitmap scaled;
    try {
        scaled = Bitmap.createScaledBitmap(src, outWidth, outHeight, false);
    } catch (OutOfMemoryError e) {
        Log.e("BitmapUtil", "OOM while scaling bitmap", e);
        return src;
    }

    Bitmap bitmap;
    try {
        bitmap = scaled.copy(config, true);
    } catch (OutOfMemoryError e) {
        Log.e("BitmapUtil", "OOM while copying bitmap", e);
        return src;
    }

    int w = bitmap.getWidth();
    int h = bitmap.getHeight();
    long whLong = (long) w * (long) h;
    if (whLong > Integer.MAX_VALUE) {
        return src;
    }
    int wh = (int) whLong;

    int[] pix = new int[wh];
    bitmap.getPixels(pix, 0, w, 0, 0, w, h);

    int wm = w - 1;
    int hm = h - 1;
    int div = radius + radius + 1;

    int[] r = new int[wh];
    int[] g = new int[wh];
    int[] b = new int[wh];

    int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
    int[] vmin = new int[Math.max(w, h)];

    int divsum = (div + 1) >> 1;
    divsum *= divsum;
    int[] dv = new int[256 * divsum];
    for (i = 0; i < dv.length; i++) {
        dv[i] = (i / divsum);
    }

    yw = yi = 0;

    int[][] stack = new int[div][3];
    int stackpointer;
    int stackstart;
    int[] sir;
    int rbs;
    int r1 = radius + 1;
    int routsum, goutsum, boutsum;
    int rinsum, ginsum, binsum;

    for (y = 0; y < h; y++) {
        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
        for (i = -radius; i <= radius; i++) {
            p = pix[yi + Math.min(wm, Math.max(i, 0))];
            sir = stack[i + radius];
            sir[0] = (p >> 16) & 0xFF;
            sir[1] = (p >> 8) & 0xFF;
            sir[2] = p & 0xFF;
            rbs = r1 - Math.abs(i);
            rsum += sir[0] * rbs;
            gsum += sir[1] * rbs;
            bsum += sir[2] * rbs;
            if (i > 0) {
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
            } else {
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
            }
        }
        stackpointer = radius;

        for (x = 0; x < w; x++) {
            r[yi] = dv[rsum];
            g[yi] = dv[gsum];
            b[yi] = dv[bsum];

            rsum -= routsum;
            gsum -= goutsum;
            bsum -= boutsum;

            stackstart = stackpointer - radius + div;
            sir = stack[stackstart % div];

            routsum -= sir[0];
            goutsum -= sir[1];
            boutsum -= sir[2];

            if (y == 0) {
                vmin[x] = Math.min(x + r1, wm);
            }
            p = pix[yw + vmin[x]];

            sir[0] = (p >> 16) & 0xFF;
            sir[1] = (p >> 8) & 0xFF;
            sir[2] = p & 0xFF;

            rinsum += sir[0];
            ginsum += sir[1];
            binsum += sir[2];

            rsum += rinsum;
            gsum += ginsum;
            bsum += binsum;

            stackpointer = (stackpointer + 1) % div;
            sir = stack[stackpointer];

            routsum += sir[0];
            goutsum += sir[1];
            boutsum += sir[2];

            rinsum -= sir[0];
            ginsum -= sir[1];
            binsum -= sir[2];

            yi++;
        }
        yw += w;
    }

    for (x = 0; x < w; x++) {
        rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
        yp = -radius * w;
        for (i = -radius; i <= radius; i++) {
            yi = Math.max(0, yp) + x;
            sir = stack[i + radius];
            sir[0] = r[yi];
            sir[1] = g[yi];
            sir[2] = b[yi];
            rbs = r1 - Math.abs(i);
            rsum += r[yi] * rbs;
            gsum += g[yi] * rbs;
            bsum += b[yi] * rbs;
            if (i > 0) {
                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];
            } else {
                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];
            }
            if (i < hm) {
                yp += w;
            }
        }
        yi = x;
        stackpointer = radius;
        for (y = 0; y < h; y++) {
            int alpha = pix[yi] & 0xFF000000;
            pix[yi] = alpha | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

            rsum -= routsum;
            gsum -= goutsum;
            bsum -= boutsum;

            stackstart = stackpointer - radius + div;
            sir = stack[stackstart % div];

            routsum -= sir[0];
            goutsum -= sir[1];
            boutsum -= sir[2];

            if (x == 0) {
                vmin[y] = Math.min(y + r1, hm) * w;
            }
            p = x + vmin[y];

            sir[0] = r[p];
            sir[1] = g[p];
            sir[2] = b[p];

            rinsum += sir[0];
            ginsum += sir[1];
            binsum += sir[2];

            rsum += rinsum;
            gsum += ginsum;
            bsum += binsum;

            stackpointer = (stackpointer + 1) % div;
            sir = stack[stackpointer];

            routsum += sir[0];
            goutsum += sir[1];
            boutsum += sir[2];

            rinsum -= sir[0];
            ginsum -= sir[1];
            binsum -= sir[2];

            yi += w;
        }
    }

    bitmap.setPixels(pix, 0, w, 0, 0, w, h);
    return bitmap;
}