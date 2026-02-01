    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (parent == null || c == null) {
            return;
        }

        if (mDivider == null) {
            return;
        }

        Context safeContext = parent.getContext() != null ? parent.getContext() : this.context;
        if (safeContext == null) {
            return;
        }

        int dividerStartInset;
        try {
            dividerStartInset = safeContext.getResources().getDimensionPixelSize(R.dimen.recycler_view_divider_start_inset);
        } catch (Exception e) {
            // Fallback to no inset if the dimension cannot be resolved
            dividerStartInset = 0;
        }

        final int left = dividerStartInset;
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (child == null) {
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }