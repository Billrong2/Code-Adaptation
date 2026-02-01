  @Override
  public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
    // Handle link touch selection/click behavior
    if (widget == null || buffer == null || event == null) {
      return Touch.onTouchEvent(widget, buffer, event);
    }

    final int action = event.getAction();
    if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
      final Layout layout = widget.getLayout();
      if (layout == null || layout.getLineCount() == 0) {
        return Touch.onTouchEvent(widget, buffer, event);
      }

      int eventX = (int) event.getX();
      int eventY = (int) event.getY();

      eventX -= widget.getTotalPaddingLeft();
      eventY -= widget.getTotalPaddingTop();

      eventX += widget.getScrollX();
      eventY += widget.getScrollY();

      if (eventY < 0) {
        return Touch.onTouchEvent(widget, buffer, event);
      }

      final int line = layout.getLineForVertical(eventY);
      final int off = layout.getOffsetForHorizontal(line, eventX);

      final ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
      if (link.length != 0) {
        if (action == MotionEvent.ACTION_UP) {
          link[0].onClick(widget);
        } else {
          Selection.setSelection(buffer,
              buffer.getSpanStart(link[0]),
              buffer.getSpanEnd(link[0]));
        }
        return true;
      }
    }

    return Touch.onTouchEvent(widget, buffer, event);
  }