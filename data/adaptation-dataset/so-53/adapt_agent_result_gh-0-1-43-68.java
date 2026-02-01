@Override
public boolean onTouchEvent(android.view.MotionEvent ev) {
    // Evaluate swipe-out only on ACTION_UP; ACTION_MOVE is a no-op here
    final int action = ev.getAction() & android.support.v4.view.MotionEventCompat.ACTION_MASK;

    // Adapter and bounds safety
    final android.support.v4.view.PagerAdapter adapter = getAdapter();
    final int count = adapter != null ? adapter.getCount() : 0;
    final int currentItem = getCurrentItem();

    final boolean hasItems = count > 0 && currentItem >= 0 && currentItem < count;
    final boolean isFirst = hasItems && currentItem == 0;
    final boolean isLast = hasItems && currentItem == count - 1;

    if (action == android.view.MotionEvent.ACTION_UP && hasItems) {
        final float x = ev.getX();
        // Ensure mStartDragX was initialized elsewhere (e.g., onInterceptTouchEvent)
        if (mListener != null) {
            if (isFirst && x > mStartDragX) {
                // Rightward swipe on first item
                mListener.onSwipeOutAtStart();
            } else if (isLast && x < mStartDragX) {
                // Leftward swipe on last item
                mListener.onSwipeOutAtEnd();
            }
        }
    }

    // Reset start drag only when not at an edge
    if (!isFirst && !isLast) {
        mStartDragX = 0f;
    }

    try {
        return super.onTouchEvent(ev);
    } catch (Exception e) {
        return false;
    }
}