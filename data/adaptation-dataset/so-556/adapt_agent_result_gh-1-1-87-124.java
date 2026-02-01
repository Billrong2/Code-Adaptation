private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	// Hard-coded DIP constants (instance-scoped behavior)
	final int CORNER_RADIUS_DIP = 4;
	final int BORDER_WIDTH_DIP = 6;

	// Basic validation
	if (bitmap == null) {
		return null;
	}
	if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
		return bitmap;
	}
	if (this.context == null || this.conferenceUser == null || this.conferenceUser.getUser() == null) {
		return bitmap;
	}

	// Resolve user-specific color from instance state
	final int borderColor = this.conferenceUser.getUser().userColor;

	try {
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int borderSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				(float) BORDER_WIDTH_DIP,
				context.getResources().getDisplayMetrics());
		final int cornerSizePx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				(float) CORNER_RADIUS_DIP,
				context.getResources().getDisplayMetrics());

		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		// prepare canvas for transfer
		paint.setAntiAlias(true);
		paint.setColor(0xFFFFFFFF);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		// draw bitmap with mask
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		// draw border using user-specific color
		paint.setXfermode(null);
		paint.setColor(borderColor);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		return output;
	} catch (OutOfMemoryError oom) {
		// Fallback to original bitmap if allocation fails
		return bitmap;
	}
}