@Override
public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);

    if (state != SCROLL_STATE_IDLE) {
        return;
    }

    RecyclerView.LayoutManager lm = getLayoutManager();
    if (!(lm instanceof LinearLayoutManager)) {
        return;
    }

    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) lm;

    final int recyclerViewWidth = getMeasuredWidth();
    if (recyclerViewWidth <= 0) {
        return;
    }

    final int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
    final int firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
    if (lastVisibleItemPosition == RecyclerView.NO_POSITION
            || firstVisibleItemPosition == RecyclerView.NO_POSITION) {
        return;
    }

    final View lastView = linearLayoutManager.findViewByPosition(lastVisibleItemPosition);
    final View firstView = linearLayoutManager.findViewByPosition(firstVisibleItemPosition);
    if (lastView == null || firstView == null) {
        return;
    }

    // distance we need to scroll, all relative to RecyclerView's own width
    final int leftMargin = (recyclerViewWidth - lastView.getWidth()) / 2;
    final int rightMargin = (recyclerViewWidth - firstView.getWidth()) / 2 + firstView.getWidth();
    final int leftEdge = lastView.getLeft();
    final int rightEdge = firstView.getRight();
    final int scrollDistanceLeft = leftEdge - leftMargin;
    final int scrollDistanceRight = rightMargin - rightEdge;

    final int halfWidth = recyclerViewWidth / 2;

    if (leftEdge > halfWidth) {
        smoothScrollBy(-scrollDistanceRight, 0);
    } else if (rightEdge < halfWidth) {
        smoothScrollBy(scrollDistanceLeft, 0);
    }
}