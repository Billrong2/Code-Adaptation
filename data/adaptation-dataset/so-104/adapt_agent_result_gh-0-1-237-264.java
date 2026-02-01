public void prepareFullscreenHandler(NonLeakingWebView webView) {
        hideToolbarDelayed = new Runnable() {

            @Override
            public void run() {
                hideToolbar();
            }
        };

        /// adapted from http://stackoverflow.com/a/16485989
        webView.setOnTouchListener(new View.OnTouchListener() {
            private float mDownX;
            private float mDownY;
            private final float SCROLL_THRESHOLD = 10f;
            private boolean isOnClick;

            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (v == null || ev == null) return false;

                switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = ev.getX();
                        mDownY = ev.getY();
                        isOnClick = true;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (isOnClick) {
                            showToolbar();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isOnClick &&
                                (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD ||
                                 Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                            isOnClick = false;
                        }
                        break;
                    default:
                        break;
                }
                // do not consume the event; allow propagation
                return false;
            }
        });
    }