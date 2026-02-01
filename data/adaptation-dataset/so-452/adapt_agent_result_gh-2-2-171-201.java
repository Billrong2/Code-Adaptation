public void collapse() {
	final View thisView = (View) this;
	if (thisView.getVisibility() == View.GONE) {
		return;
	}
	final android.view.ViewGroup.LayoutParams params = thisView.getLayoutParams();
	if (params == null) {
		return;
	}
	final int startHeight = thisView.getHeight() > 0 ? thisView.getHeight() : thisView.getMeasuredHeight();
	if (startHeight <= 0) {
		thisView.setVisibility(View.GONE);
		return;
	}
	final float density = thisView.getContext().getResources().getDisplayMetrics().density;
	final int durationMs = (int) ((startHeight / density) * 2);
	Animation animation = new Animation() {
		@Override
		protected void applyTransformation(float interpolatedTime, Transformation t) {
			params.height = startHeight - (int) (startHeight * interpolatedTime);
			thisView.requestLayout();
			if (interpolatedTime >= 1f) {
				thisView.setVisibility(View.GONE);
			}
		}

		@Override
		public boolean willChangeBounds() {
			return true;
		}
	};
	animation.setDuration(durationMs);
	thisView.startAnimation(animation);
}