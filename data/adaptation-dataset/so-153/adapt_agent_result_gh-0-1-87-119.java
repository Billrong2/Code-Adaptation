@Override
public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);

    // Snap only when scrolling is completely idle
    if (state != RecyclerView.SCROLL_STATE_IDLE) {
        return;
    }

    RecyclerView.LayoutManager lm = getLayoutManager();
    if (!(lm instanceof LinearLayoutManager)) {
        return;
    }

    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) lm;

    if (getAdapter() == null || getAdapter().getItemCount() == 0) {
        return;
    }

    int recyclerViewWidth = getMeasuredWidth();
    if (recyclerViewWidth <= 0) {
        return; // not measured yet
    }

    int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
    int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();

    if (lastVisibleItemPosition == RecyclerView.NO_POSITION
            || firstVisibleItemPosition == RecyclerView.NO_POSITION) {
        return;
    }

    View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
    View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);

    if (lastView == null || firstView == null) {
        return;
    }

    // distance we need to scroll (same calculations as before)
    int leftMargin = (recyclerViewWidth - lastView.getWidth()) / 2;
    int rightMargin = (recyclerViewWidth - firstView.getWidth()) / 2 + firstView.getWidth();
    int leftEdge = lastView.getLeft();
    int rightEdge = firstView.getRight();
    int scrollDistanceLeft = leftEdge - leftMargin;
    int scrollDistanceRight = rightMargin - rightEdge;

    int half = recyclerViewWidth / 2;

    // Only adjust when edges cross the center; otherwise do nothing
    if (leftEdge > half) {
        // center previous item
        smoothScrollBy(scrollDistanceLeft, 0);
    } else if (rightEdge < half) {
        // center next item
        smoothScrollBy(-scrollDistanceRight, 0);
    }
}
