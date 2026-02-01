@Override
public void onScrollStateChanged(int state) {
    super.onScrollStateChanged(state);
    if (state != RecyclerView.SCROLL_STATE_IDLE) {
        return;
    }

    RecyclerView.LayoutManager layoutManager = getLayoutManager();
    if (!(layoutManager instanceof LinearLayoutManager)) {
        return;
    }

    if (getAdapter() == null || getAdapter().getItemCount() <= 1) {
        return;
    }

    int viewportWidth = getMeasuredWidth();
    if (viewportWidth <= 0) {
        return;
    }

    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
    int firstVisiblePosition = linearLayoutManager.findFirstVisibleItemPosition();
    int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();

    if (firstVisiblePosition == RecyclerView.NO_POSITION || lastVisiblePosition == RecyclerView.NO_POSITION) {
        return;
    }

    View firstView = linearLayoutManager.findViewByPosition(firstVisiblePosition);
    View lastView = linearLayoutManager.findViewByPosition(lastVisiblePosition);
    if (firstView == null || lastView == null) {
        return;
    }

    int viewportCenter = viewportWidth / 2;

    // Determine which visible view is closer to the viewport center
    int firstViewCenter = firstView.getLeft() + firstView.getWidth() / 2;
    int lastViewCenter = lastView.getLeft() + lastView.getWidth() / 2;

    View closestView;
    if (Math.abs(firstViewCenter - viewportCenter) <= Math.abs(lastViewCenter - viewportCenter)) {
        closestView = firstView;
    } else {
        closestView = lastView;
    }

    // Calculate scroll distance using variable-size item centering math
    int targetLeftMargin = (viewportWidth - closestView.getWidth()) / 2;
    int currentLeft = closestView.getLeft();
    int scrollDistance = currentLeft - targetLeftMargin;

    // Only snap if the item is not already centered
    if (scrollDistance != 0) {
        smoothScrollBy(scrollDistance, 0);
    }
}
