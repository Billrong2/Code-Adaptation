public void draggable(View v) {
        if (v == null) {
            return;
        }
        v.setOnTouchListener(new View.OnTouchListener() {
            final android.graphics.PointF downPT = new android.graphics.PointF(); // touch point on ACTION_DOWN
            final android.graphics.PointF startPT = new android.graphics.PointF(); // view position on ACTION_DOWN

            @Override
            public boolean onTouch(View v, android.view.MotionEvent event) {
                if (v == null || event == null) {
                    return true;
                }
                final int action = event.getActionMasked();
                switch (action) {
                    case android.view.MotionEvent.ACTION_DOWN: {
                        downPT.x = event.getX();
                        downPT.y = event.getY();
                        startPT.x = v.getX();
                        startPT.y = v.getY();
                        break;
                    }
                    case android.view.MotionEvent.ACTION_MOVE: {
                        final float dx = event.getX() - downPT.x;
                        final float dy = event.getY() - downPT.y;
                        v.setX(startPT.x + dx);
                        v.setY(startPT.y + dy);
                        // update start position to keep dragging smooth
                        startPT.x = v.getX();
                        startPT.y = v.getY();
                        break;
                    }
                    case android.view.MotionEvent.ACTION_UP: {
                        // no-op
                        break;
                    }
                    default:
                        break;
                }
                return true; // always consume the event
            }
        });
    }