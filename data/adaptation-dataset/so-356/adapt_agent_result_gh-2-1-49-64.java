@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int measuredWidth = android.view.View.MeasureSpec.getSize(widthMeasureSpec);
    if (mBoundedWidth > 0 && mBoundedWidth < measuredWidth) {
        int measureMode = android.view.View.MeasureSpec.getMode(widthMeasureSpec);
        widthMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(mBoundedWidth, measureMode);
    }

    int measuredHeight = android.view.View.MeasureSpec.getSize(heightMeasureSpec);
    if (mBoundedHeight > 0 && mBoundedHeight < measuredHeight) {
        int measureMode = android.view.View.MeasureSpec.getMode(heightMeasureSpec);
        heightMeasureSpec = android.view.View.MeasureSpec.makeMeasureSpec(mBoundedHeight, measureMode);
    }

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}