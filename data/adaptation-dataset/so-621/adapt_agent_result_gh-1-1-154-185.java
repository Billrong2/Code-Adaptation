/**
     * Fixes a known transition memory leak on Lollipop and above by removing this Activity's
     * decor view from {@link android.transition.TransitionManager}'s internal running transitions.
     * <p>
     * This uses reflection to access internal framework state and should be called when an
     * Activity is finishing to avoid leaking its view hierarchy.
     * </p>
     *
     * @param activity the Activity whose decor view should be cleared from running transitions
     */
    public static void fixTransitionLeak(@NonNull Activity activity) {
        if (activity == null) return;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return;
        }
        final Class<?> transitionManagerClass = android.transition.TransitionManager.class;
        try {
            final java.lang.reflect.Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            final ThreadLocal<java.lang.ref.WeakReference<android.util.ArrayMap<android.view.ViewGroup, java.util.ArrayList<android.transition.Transition>>>> runningTransitions =
                    (ThreadLocal<java.lang.ref.WeakReference<android.util.ArrayMap<android.view.ViewGroup, java.util.ArrayList<android.transition.Transition>>>>)
                            runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions == null || runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            final android.util.ArrayMap<android.view.ViewGroup, java.util.ArrayList<android.transition.Transition>> map = runningTransitions.get().get();
            final android.view.View decorView = activity.getWindow().getDecorView();
            if (decorView instanceof android.view.ViewGroup && map.containsKey(decorView)) {
                map.remove(decorView);
            }
        } catch (NoSuchFieldException | IllegalAccessException | SecurityException ignored) {
            // Intentionally swallow reflection-related exceptions to avoid noisy logs
        }
    }