public void expand() {
	final View v = this;
	// Only expand if not already visible
	if (v.getVisibility() == View.VISIBLE) {
		return;
	}

	// Ensure layout params exist
	final android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
	if (lp == null) {
		return;
	}

	// Measure target height
	v.measure(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
			android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
	final int targetHeight = v.getMeasuredHeight();

	// Start from fully collapsed state
	lp.height = 0;
	v.setVisibility(View.VISIBLE);

	final Animation animation = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			if (interpolatedTime == 1f) {
				v.getLayoutParams().height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
			} else {
				v.getLayoutParams().height = (int) (targetHeight * interpolatedTime);
			}
			v.requestLayout();
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	};

	// 1dp/ms, slowed down by 2x
	final float density = v.getContext().getResources().getDisplayMetrics().density;
	animation.setDuration((int) ((targetHeight / density) * 2));
	v.startAnimation(animation);
}