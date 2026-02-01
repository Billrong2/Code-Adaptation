@Override
protected Void loadInBackground() {
    for (int progress = 0; progress < MAX_COUNT && !isReset(); progress++) {
        if (Thread.currentThread().isInterrupted()) {
            break;
        }
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        Log.d(getClass().getSimpleName(), "Progress value is " + progress);
        Log.d(getClass().getSimpleName(), "getActivity is " + getContext());
        Log.d(getClass().getSimpleName(), "this is " + this);

        LoaderWithProgressDemoActivity activity = mActivity != null ? mActivity.get() : null;
        if (activity != null) {
            activity.updateProgress(progress);
        }
    }
    return null;
}