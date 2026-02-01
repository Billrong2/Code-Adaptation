void setCursorDrawableColor(int color) {
    try {
        final java.lang.reflect.Field fCursorDrawableRes = android.widget.TextView.class.getDeclaredField("mCursorDrawableRes");
        fCursorDrawableRes.setAccessible(true);
        final int cursorDrawableResId = fCursorDrawableRes.getInt(this);
        if (cursorDrawableResId == 0) {
            return;
        }

        final java.lang.reflect.Field fEditor = android.widget.TextView.class.getDeclaredField("mEditor");
        fEditor.setAccessible(true);
        final Object editor = fEditor.get(this);
        if (editor == null) {
            return;
        }

        final java.lang.reflect.Field fCursorDrawable = editor.getClass().getDeclaredField("mCursorDrawable");
        fCursorDrawable.setAccessible(true);

        final android.graphics.drawable.Drawable drawableStart = android.support.v4.content.ContextCompat.getDrawable(getContext(), cursorDrawableResId);
        final android.graphics.drawable.Drawable drawableEnd = android.support.v4.content.ContextCompat.getDrawable(getContext(), cursorDrawableResId);
        if (drawableStart == null || drawableEnd == null) {
            return;
        }

        drawableStart.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        drawableEnd.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

        fCursorDrawable.set(editor, new android.graphics.drawable.Drawable[]{drawableStart, drawableEnd});
    } catch (Throwable ignored) {
        // Ignore reflection or platform-specific failures
    }
}