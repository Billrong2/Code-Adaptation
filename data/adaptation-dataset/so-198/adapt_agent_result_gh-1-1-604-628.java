public static void positionToast(final Toast toast, final View view, final Window window, final int offsetX, final int offsetY) {
	// Defensive checks to avoid NPEs
	if (toast == null || view == null || window == null || toast.getView() == null) {
		return;
	}

	// Toasts are positioned relative to the decor view; views are relative to their parents.
	// Gather data to convert everything into the same (decor-view-relative) coordinate system.
	final Rect visibleFrame = new Rect();
	window.getDecorView().getWindowVisibleDisplayFrame(visibleFrame);

	// Convert anchor view absolute position to decor-view-relative position
	final int[] viewLocation = new int[2];
	view.getLocationInWindow(viewLocation);
	final int viewLeft = viewLocation[0] - visibleFrame.left;
	final int viewTop = viewLocation[1] - visibleFrame.top;

	// Measure toast to determine its dimensions
	final DisplayMetrics metrics = new DisplayMetrics();
	window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
	final int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.UNSPECIFIED);
	final int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.UNSPECIFIED);
	toast.getView().measure(widthMeasureSpec, heightMeasureSpec);
	final int toastWidth = toast.getView().getMeasuredWidth();

	// Compute anchor horizontal center
	final int anchorCenterX = viewLeft + view.getWidth() / 2;

	// Position toast so its right edge aligns with the anchor view's horizontal center
	final int toastX = anchorCenterX - toastWidth + offsetX;

	// Keep vertical positioning unchanged: below the anchor view plus offset
	final int toastY = viewTop + view.getHeight() + offsetY;

	// Apply gravity and offsets
	toast.setGravity(Gravity.LEFT | Gravity.TOP, toastX, toastY);
}