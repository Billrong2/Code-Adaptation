public void expand() {
	final View v = this;
	// Guard: only expand if not already visible
	if (v.getVisibility() == View.VISIBLE) {
		return;
	}

	// Ensure LayoutParams exist
	if (v.getLayoutParams() == null) {
		v.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				0));
	}

	// Measure target height
	v.measure(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	final int targetHeight = v.getMeasuredHeight();

	// Prepare view for animation
	v.getLayoutParams().height = 0;
	v.setVisibility(View.VISIBLE);

	final float density = v.getContext().getResources().getDisplayMetrics().density;
	final int durationMs = (int) ((targetHeight / density) * 2);

	Animation a = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			v.getLayoutParams().height = interpolatedTime == 1f
					? android.view.ViewGroup.LayoutParams.WRAP_CONTENT
					: (int) (targetHeight * interpolatedTime);
			v.requestLayout();
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	};

	a.setDuration(durationMs);
	v.startAnimation(a);
}