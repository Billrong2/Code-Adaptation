private void initializePicker(final OnTimeSetListener callback) {
    try {
      // If you're only using Honeycomb+ then you can just call getTimePicker() instead of using reflection
      java.lang.reflect.Field pickerField = android.app.TimePickerDialog.class.getDeclaredField("mTimePicker");
      pickerField.setAccessible(true);
      final android.widget.TimePicker picker = (android.widget.TimePicker) pickerField.get(this);
      if (picker == null || callback == null) {
        return;
      }
      this.setCancelable(true);
      this.setButton(android.content.DialogInterface.BUTTON_NEGATIVE,
          getContext().getText(android.R.string.cancel), (android.content.DialogInterface.OnClickListener) null);
      this.setButton(android.content.DialogInterface.BUTTON_POSITIVE,
          getContext().getText(android.R.string.ok), new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
              picker.clearFocus(); // Focus must be cleared so the value change listener is called
              int hour;
              int minute;
              if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hour = picker.getHour();
                minute = picker.getMinute();
              } else {
                hour = picker.getCurrentHour();
                minute = picker.getCurrentMinute();
              }
              callback.onTimeSet(picker, hour, minute);
            }
          });
    } catch (NoSuchFieldException e) {
      // Reflection failed: mTimePicker field not found
    } catch (IllegalAccessException e) {
      // Reflection failed: cannot access mTimePicker
    } catch (Exception e) {
      // Other unexpected reflection/runtime failure
    }
  }