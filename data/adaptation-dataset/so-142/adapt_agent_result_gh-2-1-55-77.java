@Override
protected void applyTransformation(float interpolatedTime, Transformation t) {
    super.applyTransformation(interpolatedTime, t);

    if (mAnimatedView == null) {
        return;
    }

    android.view.ViewGroup.LayoutParams params = mAnimatedView.getLayoutParams();
    if (params == null) {
        return;
    }

    // Guard against invalid end height
    int safeEndHeight = mEndHeight < 0 ? 0 : mEndHeight;

    if (interpolatedTime < 1.0f) {
        if (mType == 0) {
            // Expanding
            params.height = (int) (safeEndHeight * interpolatedTime);
        } else {
            // Collapsing
            params.height = safeEndHeight - (int) (safeEndHeight * interpolatedTime);
        }
        mAnimatedView.requestLayout();
    } else {
        if (mType == 0) {
            // Expansion finished
            params.height = safeEndHeight;
            mAnimatedView.requestLayout();
        } else {
            // Collapse finished
            params.height = 0;
            mAnimatedView.setVisibility(View.GONE);
            mAnimatedView.requestLayout();
            // Preset height for future use instead of WRAP_CONTENT
            params.height = safeEndHeight;
        }
    }
}