public static Bitmap getRoundedCornerBitmap(final Bitmap sourceBitmap, final int width, final int height, final float cornerRadius, final boolean squareTL, final boolean squareTR, final boolean squareBL, final boolean squareBR) {
	// Validate inputs
	if (sourceBitmap == null) {
		return null;
	}
	if (width <= 0 || height <= 0) {
		throw new IllegalArgumentException("Width and height must be > 0");
	}

	final Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	final Canvas canvas = new Canvas(output);

	final int MASK_COLOR = 0xff424242;
	final Paint paint = new Paint();
	paint.setAntiAlias(true);

	final Rect dstRect = new Rect(0, 0, width, height);
	final RectF dstRectF = new RectF(dstRect);

	// Clear canvas
	canvas.drawARGB(0, 0, 0, 0);

	// Draw rounded rectangle mask
	paint.setColor(MASK_COLOR);
	final float radius = Math.max(0f, cornerRadius);
	canvas.drawRoundRect(dstRectF, radius, radius, paint);

	// Overlay squares to force square corners where requested
	// Top-left
	if (squareTL) {
		canvas.drawRect(0, 0, width / 2f, height / 2f, paint);
	}
	// Top-right
	if (squareTR) {
		canvas.drawRect(width / 2f, 0, width, height / 2f, paint);
	}
	// Bottom-left
	if (squareBL) {
		canvas.drawRect(0, height / 2f, width / 2f, height, paint);
	}
	// Bottom-right
	if (squareBR) {
		canvas.drawRect(width / 2f, height / 2f, width, height, paint);
	}

	// SRC_IN the source bitmap, scaling or clipping safely if needed
	paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
	final Rect srcRect = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
	canvas.drawBitmap(sourceBitmap, srcRect, dstRect, paint);

	paint.setXfermode(null);
	return output;
}