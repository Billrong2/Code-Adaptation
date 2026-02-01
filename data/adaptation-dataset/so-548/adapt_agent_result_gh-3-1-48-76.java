@Override
public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
    boolean handled = false;

    int action = event.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
        pressedSpan = getPressedSpan(textView, spannable, event);
        if (pressedSpan != null) {
            pressedSpan.setPressed(true);
            Selection.setSelection(
                    spannable,
                    spannable.getSpanStart(pressedSpan),
                    spannable.getSpanEnd(pressedSpan)
            );
            handled = true;
        }
    } else if (action == MotionEvent.ACTION_MOVE) {
        TouchableUrlSpan touchedSpan = getPressedSpan(textView, spannable, event);
        if (pressedSpan != null && touchedSpan != pressedSpan) {
            pressedSpan.setPressed(false);
            pressedSpan = null;
            Selection.removeSelection(spannable);
        }
        // do not mark handled; allow propagation
    } else {
        if (pressedSpan != null) {
            pressedSpan.setPressed(false);
            // delegate to super so the click is performed
            super.onTouchEvent(textView, spannable, event);
            handled = true;
        }
        pressedSpan = null;
        Selection.removeSelection(spannable);
    }

    return handled;
}