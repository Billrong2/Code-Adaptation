private void testAnimation() {
    final int ANIMATION_DURATION_MS = 1500;
    final ImageView imageView = (ImageView) findViewById(R.id.imageView);
    if (imageView == null) {
        return;
    }

    try {
        final android.graphics.drawable.Drawable drawable = getResources().getDrawable(R.drawable.image);
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        final BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        if (bitmapDrawable.getBitmap() == null) {
            return;
        }

        imageView.setImageDrawable(bitmapDrawable);

        final AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
        final AnimateColorMatrixColorFilter filter = new AnimateColorMatrixColorFilter(evaluator.getColorMatrix());
        bitmapDrawable.setColorFilter(filter.getColorFilter());

        final ObjectAnimator animator = ObjectAnimator.ofObject(
                filter,
                "colorMatrix",
                evaluator,
                evaluator.getColorMatrix()
        );

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bitmapDrawable.setColorFilter(filter.getColorFilter());
            }
        });
        animator.setDuration(ANIMATION_DURATION_MS);
        animator.start();
    } catch (Resources.NotFoundException e) {
        // Drawable resource not found; safely ignore
    }
}