public static void setEdgeEffectColor(final android.widget.EdgeEffect edgeEffect, @android.support.annotation.ColorRes final int colorRes) {
    if (edgeEffect == null) {
        return;
    }
    try {
        // Resolve color from @ColorRes using EdgeEffect's internal Context
        int resolvedColor = 0;
        android.content.Context context = null;
        try {
            java.lang.reflect.Field contextField = android.widget.EdgeEffect.class.getDeclaredField("mContext");
            contextField.setAccessible(true);
            context = (android.content.Context) contextField.get(edgeEffect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context != null) {
            resolvedColor = android.support.v4.content.ContextCompat.getColor(context, colorRes);
        } else {
            // Fallback: do nothing if context cannot be resolved
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            edgeEffect.setColor(resolvedColor);
            return;
        }
        final java.lang.reflect.Field edgeField = android.widget.EdgeEffect.class.getDeclaredField("mEdge");
        final java.lang.reflect.Field glowField = android.widget.EdgeEffect.class.getDeclaredField("mGlow");
        edgeField.setAccessible(true);
        glowField.setAccessible(true);
        final android.graphics.drawable.Drawable edge = (android.graphics.drawable.Drawable) edgeField.get(edgeEffect);
        final android.graphics.drawable.Drawable glow = (android.graphics.drawable.Drawable) glowField.get(edgeEffect);
        if (edge != null) {
            edge.setColorFilter(resolvedColor, android.graphics.PorterDuff.Mode.SRC_IN);
            edge.setCallback(null); // free up any references
        }
        if (glow != null) {
            glow.setColorFilter(resolvedColor, android.graphics.PorterDuff.Mode.SRC_IN);
            glow.setCallback(null); // free up any references
        }
    } catch (final Exception e) {
        e.printStackTrace();
    }
}