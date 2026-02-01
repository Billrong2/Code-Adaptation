@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// Aspect-ratio-preserving measurement for ImageView.
	// One dimension is auto-fitted based on the other while keeping the drawable's intrinsic ratio.
	// Adapted from a common Stack Overflow solution discussing ImageView auto-fit behavior.

	Drawable drawable = getDrawable();

	// If there is no drawable, or we cannot safely determine its size, defer to default behavior.
	if (drawable == null) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		return;
	}

	int intrinsicWidth = drawable.getIntrinsicWidth();
	int intrinsicHeight = drawable.getIntrinsicHeight();

	// Guard against invalid intrinsic sizes to avoid divide-by-zero.
	if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		return;
	}

	int width = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
	int height = android.view.View.MeasureSpec.getSize(heightMeasureSpec);

	// Preserve aspect ratio by scaling the smaller-constrained dimension.
	if (width >= height && height > 0) {
		height = (int) Math.ceil(width * (float) intrinsicHeight / intrinsicWidth);
	} else if (width > 0) {
		width = (int) Math.ceil(height * (float) intrinsicWidth / intrinsicHeight);
	}

	setMeasuredDimension(width, height);
}