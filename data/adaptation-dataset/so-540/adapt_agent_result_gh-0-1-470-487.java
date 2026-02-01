public static int getTextViewHeight(TextView textView) {
    if (textView == null) {
        return 0;
    }
    final Context context = textView.getContext();
    if (context == null) {
        return 0;
    }
    final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    if (wm == null) {
        return 0;
    }

    final Display display = wm.getDefaultDisplay();
    final Point size = new Point();
    display.getSize(size);
    final int screenWidthPx = size.x;

    int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(screenWidthPx, View.MeasureSpec.AT_MOST);
    int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    textView.measure(widthMeasureSpec, heightMeasureSpec);
    return textView.getMeasuredHeight();
}