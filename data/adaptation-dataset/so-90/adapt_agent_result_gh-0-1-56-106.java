@Override
public boolean onTouch(View v, MotionEvent event) {
    if (v == null || event == null) {
        return false;
    }
    final float milliSec = 1000f;
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            MyLog.d(this, "onTouch ACTION_DOWN");
            timeDown = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            // Do not consume ACTION_DOWN so other handlers (e.g. click) can proceed
            return false;
        }
        case MotionEvent.ACTION_UP: {
            MyLog.d(this, "onTouch ACTION_UP");
            final long timeUp = System.currentTimeMillis();
            final float upX = event.getX();
            final float upY = event.getY();

            final float deltaX = downX - upX;
            final float absDeltaX = Math.abs(deltaX);
            final float deltaY = downY - upY;
            final float absDeltaY = Math.abs(deltaY);
            final long time = timeUp - timeDown;

            if (absDeltaY > maxOffPath) {
                MyLog.v(this, String.format("absDeltaY=%.2f, maxOffPath=%.2f", absDeltaY, maxOffPath));
                return v.performClick();
            }

            if (absDeltaX > minDistance && absDeltaX > (time * velocity) / milliSec) {
                if (deltaX < 0) {
                    onLeftToRightSwipe(v);
                    return true;
                }
                if (deltaX > 0) {
                    onRightToLeftSwipe(v);
                    return true;
                }
            } else {
                MyLog.v(this, String.format("absDeltaX=%.2f, minDistance=%.2f, cond1=%b", absDeltaX, minDistance, (absDeltaX > minDistance)));
                MyLog.v(this, String.format("absDeltaX=%.2f, time=%d, velocity=%d, threshold=%.2f, cond2=%b", absDeltaX, time, velocity, (time * velocity) / milliSec, (absDeltaX > (time * velocity) / milliSec)));
            }

            return v.performClick();
        }
        default: {
            break;
        }
    }
    return false;
}
