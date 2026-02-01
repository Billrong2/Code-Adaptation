@Override
public boolean onTouch(View v, MotionEvent event) {
    if (v == null || event == null) {
        return false;
    }
    switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN: {
            MyLog.i(this, "ACTION_DOWN");
            timeDown = System.currentTimeMillis();
            downX = event.getX();
            downY = event.getY();
            // Do not consume the event on ACTION_DOWN
            return false;
        }
        case MotionEvent.ACTION_UP: {
            MyLog.i(this, "ACTION_UP");
            final long timeUp = System.currentTimeMillis();
            final float upX = event.getX();
            final float upY = event.getY();

            final float deltaX = downX - upX;
            final float absDeltaX = Math.abs(deltaX);
            final float deltaY = downY - upY;
            final float absDeltaY = Math.abs(deltaY);
            final long time = timeUp - timeDown;

            // Too much vertical movement: treat as click
            if (absDeltaY > maxOffPath) {
                MyLog.i(this, "Vertical deviation too large, performing click");
                return v.performClick();
            }

            // Horizontal swipe detection (distance and velocity)
            final float velocityThreshold = (time > 0) ? (time * (float) velocity / 1000f) : 0f;
            if (absDeltaX > minDistance && absDeltaX > velocityThreshold) {
                if (deltaX < 0) {
                    onLeftToRightSwipe(v);
                    return true;
                } else if (deltaX > 0) {
                    onRightToLeftSwipe(v);
                    return true;
                }
            } else {
                MyLog.i(this, "No swipe detected: absDeltaX=" + absDeltaX + ", minDistance=" + minDistance + ", velocityThreshold=" + velocityThreshold);
            }
            break;
        }
        default:
            break;
    }
    // Default: do not consume the event
    return false;
}