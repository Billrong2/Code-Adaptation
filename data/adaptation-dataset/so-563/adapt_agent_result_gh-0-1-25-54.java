public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        if (layoutManager == null) {
            return RecyclerView.NO_POSITION;
        }

        final View snapView = findSnapView(layoutManager);
        if (snapView == null) {
            return RecyclerView.NO_POSITION;
        }

        final int itemCount = layoutManager.getItemCount();
        if (itemCount <= 0) {
            return RecyclerView.NO_POSITION;
        }

        final int pageSize = mPageSize > 0 ? mPageSize : 1;
        final int currentPosition = layoutManager.getPosition(snapView);
        int targetPosition = currentPosition;

        if (layoutManager.canScrollHorizontally()) {
            if (velocityX < 0) {
                targetPosition = currentPosition - pageSize;
            } else if (velocityX > 0) {
                targetPosition = currentPosition + pageSize;
            }
        } else if (layoutManager.canScrollVertically()) {
            if (velocityY < 0) {
                targetPosition = currentPosition - pageSize;
            } else if (velocityY > 0) {
                targetPosition = currentPosition + pageSize;
            }
        }

        final int firstItem = 0;
        final int lastItem = itemCount - 1;
        targetPosition = Math.min(lastItem, Math.max(firstItem, targetPosition));
        return targetPosition;
    }