@Override
public boolean onFling(android.view.MotionEvent e1, android.view.MotionEvent e2, float velocityX, float velocityY) {
    try {
        // Guard against null motion events
        if (e1 == null || e2 == null) {
            return false;
        }
        // Off-path check
        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
            return false;
        // Right-to-left swipe -> next image
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            nextImage();
        }
        // Left-to-right swipe -> previous image
        else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            previousImage();
        }
    } catch (Exception e) {
        // preserve existing behavior: swallow exception
    }
    return false;
}