@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // First pass: let ViewPager measure itself
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    final boolean isWrapContentHeight = View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.AT_MOST;

    // Validate required state
    if (getChildCount() <= 0 || datesInMonth == null || datesInMonth.isEmpty()) {
        // Nothing meaningful to measure against; keep the measured dimensions
        return;
    }

    final int measuredWidth = getMeasuredWidth();
    final int exactWidthSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth, View.MeasureSpec.EXACTLY);

    // Constants used for calendar calculations
    final int DAYS_IN_WEEK = 7;
    final int EXTRA_HEIGHT_OFFSET = 3;

    // Derive number of rows from data
    int rows = datesInMonth.size() / DAYS_IN_WEEK;
    if (rows <= 0) {
        rows = 1; // hard guard against division by zero
    }

    if (sixWeeksInCalendar) {
        rows = 6;
    }

    // Compute and cache rowHeight on first wrap-content measurement
    if (rowHeight <= 0 && isWrapContentHeight) {
        View firstChild = getChildAt(0);
        if (firstChild != null) {
            // Allow the child to wrap so we can infer a per-row height
            firstChild.measure(exactWidthSpec,
                    View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), View.MeasureSpec.AT_MOST));

            int childHeight = firstChild.getMeasuredHeight();
            if (childHeight > 0 && rows > 0) {
                rowHeight = childHeight / rows;
            }
        }
    }

    // Fallback if rowHeight is still invalid
    if (rowHeight <= 0) {
        rowHeight = getMeasuredHeight() / rows;
    }

    // Final calendar-specific height
    int calendarHeight = (rowHeight * rows) + EXTRA_HEIGHT_OFFSET;

    int exactHeightSpec = View.MeasureSpec.makeMeasureSpec(calendarHeight, View.MeasureSpec.EXACTLY);

    // Always remeasure with exact calendar height
    super.onMeasure(exactWidthSpec, exactHeightSpec);
}