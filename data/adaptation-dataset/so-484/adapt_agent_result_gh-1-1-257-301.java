@CheckResult
public static ViewAction waitForViewMatch(@NonNull final Matcher<View> viewMatcher, final long millis) {
    if (viewMatcher == null) {
        throw new IllegalArgumentException("viewMatcher must not be null");
    }
    return new ViewAction() {
        @Override
        public Matcher<View> getConstraints() {
            return isRoot();
        }

        @Override
        public String getDescription() {
            return "wait for a view matching <" + viewMatcher + "> during " + millis + " millis.";
        }

        @Override
        public void perform(final UiController uiController, final View view) {
            uiController.loopMainThreadUntilIdle();
            final long startTime = SystemClock.elapsedRealtime();
            final long endTime = startTime + millis;

            do {
                for (View child : TreeIterables.breadthFirstViewTraversal(view)) {
                    if (viewMatcher.matches(child)) {
                        return;
                    }
                }
                uiController.loopMainThreadForAtLeast(50);
            } while (SystemClock.elapsedRealtime() <= endTime);

            throw new PerformException.Builder()
                    .withActionDescription(getDescription())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(new TimeoutException())
                    .build();
        }
    };
}