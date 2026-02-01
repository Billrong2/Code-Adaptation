private Intent assertResultEquals(int resultCode) {
    if (welcomeActivity == null) {
        throw new AssertionError("welcomeActivity is null; activity was not initialized before asserting result.");
    }
    try {
        final Field resultCodeField = android.app.Activity.class.getDeclaredField("mResultCode");
        resultCodeField.setAccessible(true);
        int actualResultCode = (Integer) resultCodeField.get(welcomeActivity);
        assertThat(actualResultCode, is(resultCode));

        final Field resultDataField = android.app.Activity.class.getDeclaredField("mResultData");
        resultDataField.setAccessible(true);
        return (android.content.Intent) resultDataField.get(welcomeActivity);
    } catch (NoSuchFieldException e) {
        throw new RuntimeException("Looks like the Android Activity class has changed it's private fields for mResultCode or mResultData. Time to update the reflection code.", e);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}