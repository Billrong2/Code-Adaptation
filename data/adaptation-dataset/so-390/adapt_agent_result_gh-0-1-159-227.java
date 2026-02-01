public static void addSelectOnFocusToText(final Text text) {
	// Provenance:
	// Adapted from a Stack Overflow solution addressing SWT Text select-on-focus behavior.
	// See also Eclipse Bug 46059: FocusIn is fired before MouseDown, which can cancel selection.
	// The logic below preserves the original event ordering and asynchronous behavior exactly.
	if (text == null || text.isDisposed())
		return;

	Listener listener = new Listener() {

		private boolean hasFocus = false;
		private boolean hadFocusOnMousedown = false;

		public void handleEvent(Event e) {
			switch (e.type) {
			case SWT.FocusIn: {
				Text t = (Text) e.widget;

				// Covers the case where the user focuses the control by keyboard.
				t.selectAll();

				// Mouse focus is special because SWT.FocusIn is fired before SWT.MouseDown
				// and the mouse down cancels the selection. Track focus state asynchronously
				// so it is updated after the MouseDown event (see Eclipse bug 46059).
				t.getDisplay().asyncExec(new Runnable() {
					public void run() {
						hasFocus = true;
					}
				});

				break;
			}
			case SWT.FocusOut: {
				hasFocus = false;
				((Text) e.widget).clearSelection();
				break;
			}
			case SWT.MouseDown: {
				// Remember whether the control already had focus on mouse down.
				hadFocusOnMousedown = hasFocus;
				break;
			}
			case SWT.MouseUp: {
				Text t = (Text) e.widget;
				if (t.getSelectionCount() == 0 && !hadFocusOnMousedown) {
					t.selectAll();
				}
				break;
			}
			}
		}
	};

	text.addListener(SWT.FocusIn, listener);
	text.addListener(SWT.FocusOut, listener);
	text.addListener(SWT.MouseDown, listener);
	text.addListener(SWT.MouseUp, listener);
}