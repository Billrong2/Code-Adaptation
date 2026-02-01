public static void fixCalendarViewIfJellyBean(android.widget.CalendarView calendarView) {
        try {
            Object object = calendarView;
            java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.getName().equals("mDelegate")) { // the CalendarViewLegacyDelegate instance is stored in this variable
                    field.setAccessible(true);
                    object = field.get(object);
                    break;
                }
            }

            java.lang.reflect.Field field = object.getClass().getDeclaredField("mDateTextSize"); // text size integer value
            field.setAccessible(true);
            final int mDateTextSize = (Integer) field.get(object);

            field = object.getClass().getDeclaredField("mListView"); // main ListView
            field.setAccessible(true);
            Object innerObject = field.get(object);

            java.lang.reflect.Method method = innerObject.getClass().getMethod(
                    "setOnHierarchyChangeListener", android.view.ViewGroup.OnHierarchyChangeListener.class); // we need to set the OnHierarchyChangeListener
            method.setAccessible(true);
            method.invoke(innerObject, (Object) new android.view.ViewGroup.OnHierarchyChangeListener() {

                @Override
                public void onChildViewAdded(android.view.View parent, android.view.View child) {
                    try {
                        Object object = child;
                        java.lang.reflect.Field[] fields = object.getClass().getDeclaredFields();
                        for (java.lang.reflect.Field field : fields) {
                            if (field.getName().equals("mMonthNumDrawPaint")) { // the paint is stored inside the view
                                field.setAccessible(true);
                                object = field.get(object);
                                java.lang.reflect.Method method = object.getClass()
                                        .getDeclaredMethod("setTextSize", float.class); // finally set text size
                                method.setAccessible(true);
                                method.invoke(object, (float) mDateTextSize);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        android.util.Log.e("DateTimeWidget", e.getMessage(), e);
                    }
                }

                @Override
                public void onChildViewRemoved(android.view.View parent, android.view.View child) {
                }
            });
        } catch (Exception e) {
            android.util.Log.e("DateTimeWidget", e.getMessage(), e);
        }
    }