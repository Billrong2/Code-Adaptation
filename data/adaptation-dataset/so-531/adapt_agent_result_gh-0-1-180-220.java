@Override
public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	// Mark this fling as user initiated and reset any ongoing animation state
	userCreatedTouchEvent = true;
	ongoingAnimation = false;
	inAnimation = null;
	outAnimation = null;

	// Keep original velocity clamping and duration calculation
	final float VELOCITY_MAX = 2500f;
	final float VELOCITY_MIN = 1000f;
	final float VELOCITY_OFFSET = 600f;
	final int DURATION_SCALE = 500000;

	float velX = Math.abs(velocityX);
	if (velX > VELOCITY_MAX) {
		velX = VELOCITY_MAX;
	} else if (velX < VELOCITY_MIN) {
		velX = VELOCITY_MIN;
	}
	velX -= VELOCITY_OFFSET;
	int speed = (int) Math.floor((1f / velX) * DURATION_SCALE);
	setAnimationDuration(speed);

	// Determine fling direction safely
	int keyCode;
	if (e1 != null && e2 != null && isScrollingLeft(e1, e2)) {
		keyCode = KeyEvent.KEYCODE_DPAD_LEFT;
	} else {
		keyCode = KeyEvent.KEYCODE_DPAD_RIGHT;
	}

	// Dispatch DPAD navigation with a concrete synthetic fling event
	KeyEvent flingEvent = new FlingKeyEvent();
	onKeyDown(keyCode, flingEvent);

	return true;
}