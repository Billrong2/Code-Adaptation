public static android.support.test.espresso.ViewAction setTime(final int hours, final int minutes) {
    return new android.support.test.espresso.ViewAction() {
      @Override
      public void perform(android.support.test.espresso.UiController uiController, android.view.View view) {
        if (view == null) {
          return;
        }
        if (!(view instanceof com.wdullaer.materialdatetimepicker.time.RadialPickerLayout)) {
          return;
        }
        // Validate time ranges defensively
        if (hours < 0 || hours > 23 || minutes < 0 || minutes > 59) {
          return;
        }
        final com.wdullaer.materialdatetimepicker.time.RadialPickerLayout radialPickerLayout =
            (com.wdullaer.materialdatetimepicker.time.RadialPickerLayout) view;
        radialPickerLayout.setTime(new com.wdullaer.materialdatetimepicker.time.Timepoint(hours, minutes, 0));
      }

      @Override
      public String getDescription() {
        return "set time";
      }

      @Override
      public org.hamcrest.Matcher<android.view.View> getConstraints() {
        return org.hamcrest.Matchers.allOf(
            android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom(
                com.wdullaer.materialdatetimepicker.time.RadialPickerLayout.class),
            android.support.test.espresso.matcher.ViewMatchers.isDisplayed());
      }
    };
  }