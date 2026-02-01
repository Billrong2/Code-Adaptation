public boolean onTouch(View v, MotionEvent event)
{
	// basic hardening
	if (event == null)
		return false;

	// initialize per-view configuration once
	if (config == null && v != null)
	{
		config = ViewConfiguration.get(v.getContext());
		minSwipeDistance = config.getScaledTouchSlop();
	}

	// track the touched view across events
	if (v != null)	
		this.view = v;

	boolean thresholdCrossed = false;

	switch (event.getAction())
	{
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
		if (listener != null && view != null)
			listener.onStartSwipe(view);
		return true;

		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			upX = event.getX();
			upY = event.getY();

			float deltaX = downX - upX;
			float deltaY = downY - upY;

			// horizontal swipe has precedence
			if (Math.abs(deltaX) > minSwipeDistance)
			{
				thresholdCrossed = true;
				if (listener != null && view != null)
				{
					if (deltaX < 0)
						listener.onRightSwipe(view);
					else
						listener.onLeftSwipe(view);
				}
			}
			// vertical swipe only if horizontal was not handled
			else if (Math.abs(deltaY) > minSwipeDistance)
			{
				thresholdCrossed = true;
				if (listener != null && view != null)
				{
					if (deltaY < 0)
						listener.onDownSwipe(view);
					else
						listener.onUpSwipe(view);
				}
			}

			// always notify stop on finger lift
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				if (listener != null && view != null)
					listener.onStopSwipe(view);
			}

			return thresholdCrossed;
	}

	return false;
}