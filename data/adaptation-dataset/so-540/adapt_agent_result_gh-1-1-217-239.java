public static int getMeasuredHeight(final View view, float widthRatio) {
        if (view == null) {
            return 0;
        }

        final Context context = view.getContext();
        if (context == null) {
            return 0;
        }

        final WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) {
            return 0;
        }

        // Clamp widthRatio to a safe range
        if (widthRatio <= 0f) {
            widthRatio = 1f;
        }
        else if (widthRatio > 1f) {
            widthRatio = 1f;
        }

        final Display display = windowManager.getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);

        final int effectiveWidth = (int) (size.x * widthRatio);
        Log.d(TAG, "getMeasuredHeight(): effectiveWidth=" + effectiveWidth);

        final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(effectiveWidth, View.MeasureSpec.AT_MOST);
        final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(widthMeasureSpec, heightMeasureSpec);

        return view.getMeasuredHeight();
    }