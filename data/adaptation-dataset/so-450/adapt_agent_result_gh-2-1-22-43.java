public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition) {
    // Adapted from a Stack Overflow answer: matcher for the nth child of a parent view
    return new TypeSafeMatcher<View>() {
        @Override
        public void describeTo(final Description description) {
            description.appendText("with " + childPosition + " child view of type parentMatcher");
        }

        @Override
        public boolean matchesSafely(final View view) {
            if (!(view.getParent() instanceof ViewGroup)) {
                return parentMatcher.matches(view.getParent());
            }

            final ViewGroup parentGroup = (ViewGroup) view.getParent();
            return parentMatcher.matches(view.getParent())
                    && parentGroup.getChildAt(childPosition).equals(view);
        }
    };
}