public static void enableOptionalMenuIcons(final android.content.Context context, final android.view.Menu menu) {
        // Only enable icons for LTR layouts to avoid RTL layout issues
        try {
            final int layoutDirection = android.text.TextUtils.getLayoutDirectionFromLocale(
                    context.getResources().getConfiguration().locale);
            if (layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL) {
                return;
            }
        } catch (Exception e) {
            // If layout direction cannot be determined, fail safe and do nothing
            android.util.Log.w(TAG, "enableOptionalMenuIcons: unable to determine layout direction", e);
            return;
        }

        // Only attempt reflection on supported menu implementations
        if (!(menu instanceof android.support.v7.view.menu.MenuBuilder)) {
            return;
        }

        try {
            final java.lang.reflect.Method method = menu.getClass().getDeclaredMethod(
                    "setOptionalIconsVisible", Boolean.TYPE);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (NoSuchMethodException e) {
            android.util.Log.e(TAG, "enableOptionalMenuIcons: method not found", e);
        } catch (Exception e) {
            android.util.Log.e(TAG, "enableOptionalMenuIcons: failed to enable optional icons", e);
        }
    }