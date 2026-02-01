@Override
public boolean onTouchEvent(MotionEvent ev) {
    if (ev == null) {
        return false;
    }

    final int action = ev.getAction();

    if (action == MotionEvent.ACTION_CANCEL) {
        final int pointerCount = MotionEventCompat.getPointerCount(ev);
        final int actionIndex = MotionEventCompat.getActionIndex(ev);

        if (actionIndex < 0 || actionIndex >= pointerCount) {
            // Inconsistent pointer state, consume to avoid crash
            mActivePointerId = -1;
            return true;
        }

        mActivePointerId = MotionEventCompat.getPointerId(ev, actionIndex);
        final int index = MotionEventCompat.findPointerIndex(ev, mActivePointerId);

        if (index > -1 && index < pointerCount) {
            try {
                return super.onTouchEvent(ev);
            } catch (Exception e) {
                Crashlytics.logException(e);
                return true;
            }
        } else {
            // Invalid pointer index during cancel, consume safely
            mActivePointerId = -1;
            return true;
        }
    } else if (action == MotionEventCompat.ACTION_POINTER_DOWN && super.onTouchEvent(ev)) {
        final int index = MotionEventCompat.getActionIndex(ev);
        if (index > -1 && index < MotionEventCompat.getPointerCount(ev)) {
            mActivePointerId = MotionEventCompat.getPointerId(ev, index);
        }
        return false;
    } else if (action == MotionEventCompat.ACTION_POINTER_UP && super.onTouchEvent(ev)) {
        onSecondaryPointerUp(ev);
        return false;
    } else if (action == MotionEvent.ACTION_DOWN && super.onTouchEvent(ev)) {
        if (MotionEventCompat.getPointerCount(ev) > 0) {
            mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
        }
        return false;
    }

    return super.onTouchEvent(ev);
}