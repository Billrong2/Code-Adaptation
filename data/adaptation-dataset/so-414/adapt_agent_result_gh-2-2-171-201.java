public void collapse() {
	final View v = (View) this;

	// Do nothing if already gone
	if (v.getVisibility() == View.GONE) {
		return;
	}

	final android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
	if (lp == null) {
		return;
	}

	// Use current height as the starting point; do not re-measure
	final int initialHeight = lp.height > 0 ? lp.height : v.getMeasuredHeight();
	if (initialHeight <= 0) {
		v.setVisibility(View.GONE);
		return;
	}

	final Animation animation = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			if (interpolatedTime >= 1f) {
				lp.height = 0;
				v.setVisibility(View.GONE);
			} else {
				lp.height = initialHeight - (int) (initialHeight * interpolatedTime);
			}
			v.requestLayout();
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	};

	// 1dp/ms, doubled to slow the collapse
	final float density = v.getContext().getResources().getDisplayMetrics().density;
	animation.setDuration((int) ((initialHeight / density) * 2));
	v.startAnimation(animation);
}