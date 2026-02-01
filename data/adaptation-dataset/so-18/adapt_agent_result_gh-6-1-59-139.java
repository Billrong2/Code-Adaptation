public TouchImageView(final android.content.Context context) {
    super(context);
    if (context == null) {
        return;
    }
    this.context = context;

    setClickable(true);

    // Initialize matrix-related state defensively
    if (matrix == null) {
        matrix = new android.graphics.Matrix();
    }
    matrix.setTranslate(1f, 1f);
    m = new float[9];
    setImageMatrix(matrix);
    setScaleType(android.widget.ImageView.ScaleType.MATRIX);

    // Initialize gesture detectors once
    mScaleDetector = new android.view.ScaleGestureDetector(context, new ScaleListener());
    gdt = new android.view.GestureDetector(context, new GestureListener());

    setOnTouchListener(new android.view.View.OnTouchListener() {
        @Override
        public boolean onTouch(android.view.View v, android.view.MotionEvent event) {
            if (event == null) {
                return false;
            }

            // Feed all events to detectors
            mScaleDetector.onTouchEvent(event);
            gdt.onTouchEvent(event);

            matrix.getValues(m);
            float x = m[android.graphics.Matrix.MTRANS_X];
            float y = m[android.graphics.Matrix.MTRANS_Y];
            android.graphics.PointF curr = new android.graphics.PointF(event.getX(), event.getY());

            switch (event.getActionMasked()) {
                case android.view.MotionEvent.ACTION_DOWN:
                    last.set(curr.x, curr.y);
                    start.set(curr.x, curr.y);
                    mode = DRAG; // always enter DRAG on down
                    break;

                case android.view.MotionEvent.ACTION_MOVE:
                    if (mode == DRAG) {
                        float deltaX = curr.x - last.x;
                        float deltaY = curr.y - last.y;
                        float scaleWidth = Math.round(origWidth * saveScale);
                        float scaleHeight = Math.round(origHeight * saveScale);

                        if (scaleWidth < width) {
                            deltaX = 0;
                            if (y + deltaY > 0) {
                                deltaY = -y;
                            } else if (y + deltaY < -bottom) {
                                deltaY = -(y + bottom);
                            }
                        } else if (scaleHeight < height) {
                            deltaY = 0;
                            if (x + deltaX > 0) {
                                deltaX = -x;
                            } else if (x + deltaX < -right) {
                                deltaX = -(x + right);
                            }
                        } else {
                            if (x + deltaX > 0) {
                                deltaX = -x;
                            } else if (x + deltaX < -right) {
                                deltaX = -(x + right);
                            }

                            if (y + deltaY > 0) {
                                deltaY = -y;
                            } else if (y + deltaY < -bottom) {
                                deltaY = -(y + bottom);
                            }
                        }
                        matrix.postTranslate(deltaX, deltaY);
                        last.set(curr.x, curr.y);
                    }
                    break;

                case android.view.MotionEvent.ACTION_UP:
                    mode = NONE;
                    int xDiff = (int) Math.abs(curr.x - start.x);
                    int yDiff = (int) Math.abs(curr.y - start.y);
                    if (xDiff < CLICK && yDiff < CLICK) {
                        performClick();
                    }
                    break;

                case android.view.MotionEvent.ACTION_POINTER_UP:
                    mode = NONE;
                    break;
            }

            setImageMatrix(matrix);
            invalidate();
            return true;
        }
    });
}