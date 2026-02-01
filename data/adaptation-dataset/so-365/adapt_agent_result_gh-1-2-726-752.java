private void drawTextOnCanvasWithMagnifier(final android.graphics.Canvas canvas, final String text, final float x, final float y, final android.graphics.Paint paint) {
	if (canvas == null || paint == null) {
		return;
	}

	final String safeText = (text != null) ? text : "";

	// Draw normally on pre-Jelly Bean devices
	if (android.os.Build.VERSION.SDK_INT <= 15) {
		canvas.drawText(safeText, x, y, paint);
		return;
	}

	// Workaround for Jelly Bean+ where small text/strokes may render too thin or disappear
	final float MAGNIFIER_SCALE = 1000f;
	final float originalTextSize = paint.getTextSize();
	final float originalStrokeWidth = paint.getStrokeWidth();

	canvas.save();
	try {
		canvas.scale(1f / MAGNIFIER_SCALE, 1f / MAGNIFIER_SCALE);
		paint.setTextSize(originalTextSize * MAGNIFIER_SCALE);
		paint.setStrokeWidth(originalStrokeWidth * MAGNIFIER_SCALE);
		canvas.drawText(safeText, x * MAGNIFIER_SCALE, y * MAGNIFIER_SCALE, paint);
	} finally {
		canvas.restore();
		paint.setTextSize(originalTextSize);
		paint.setStrokeWidth(originalStrokeWidth);
	}
}