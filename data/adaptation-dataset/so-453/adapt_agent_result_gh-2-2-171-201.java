public void collapse() {
	final android.view.View v = (android.view.View) this;

	// Only collapse if not already gone
	if (v.getVisibility() == android.view.View.GONE) {
		return;
	}

	final android.view.ViewGroup.LayoutParams lp = v.getLayoutParams();
	if (lp == null) {
		return;
	}

	final int initialHeight = v.getMeasuredHeight();
	if (initialHeight <= 0) {
		v.setVisibility(android.view.View.GONE);
		return;
	}

	final float density = v.getContext().getResources().getDisplayMetrics().density;
	final int durationScale = 2;
	final int minDurationMs = 100;
	int duration = (int) ((initialHeight / density) * durationScale);
	if (duration < minDurationMs) {
		duration = minDurationMs;
	}

	android.view.animation.Animation a = new android.view.animation.Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, android.view.animation.Transformation t) {
			lp.height = (int) (initialHeight * (1f - interpolatedTime));
			v.requestLayout();
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	};

	a.setDuration(duration);
	a.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
		public void onAnimationStart(android.view.animation.Animation animation) {
		}

		public void onAnimationRepeat(android.view.animation.Animation animation) {
		}

		public void onAnimationEnd(android.view.animation.Animation animation) {
			v.setVisibility(android.view.View.GONE);
		}
	});

	v.startAnimation(a);
}