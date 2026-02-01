public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {
    return new ViewAction() {
      @Override
      public void perform(final UiController uiController, final View view) {
        if (!(view instanceof DayPickerView)) {
          Timber.e("View is not an instance of DayPickerView: %s", view);
          return;
        }

        final DayPickerView dayPickerView = (DayPickerView) view;

        try {
          final Field controllerField = DayPickerView.class.getDeclaredField("mController");
          controllerField.setAccessible(true);
          final Object controllerObject = controllerField.get(dayPickerView);

          if (!(controllerObject instanceof DatePickerController)) {
            Timber.e("mController is not a DatePickerController: %s", controllerObject);
            return;
          }

          final DatePickerController controller = (DatePickerController) controllerObject;
          controller.onDayOfMonthSelected(year, monthOfYear, dayOfMonth);
        } catch (Exception e) {
          Timber.e(e, "Failed to set date via DayPickerView reflection");
        }
      }

      @Override
      public String getDescription() {
        return "set date";
      }

      @Override
      public Matcher<View> getConstraints() {
        return allOf(isAssignableFrom(DayPickerView.class), isDisplayed());
      }
    };
  }