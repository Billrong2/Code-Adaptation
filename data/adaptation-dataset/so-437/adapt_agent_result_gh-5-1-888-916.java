public static String getContactDisplayNameByNumber(final android.content.Context context, final String number) {
    if (number == null || number.length() == 0) {
        return "";
    }

    android.util.Log.d("F.Util.contactLookup", "Looking up contact for number: " + number);

    android.database.Cursor contactLookup = null;
    String result = number; // fallback to original number

    try {
        android.net.Uri uri = android.net.Uri.withAppendedPath(
                android.provider.ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                android.net.Uri.encode(number));

        android.content.ContentResolver contentResolver = context.getContentResolver();
        contactLookup = contentResolver.query(
                uri,
                new String[] {
                        android.provider.BaseColumns._ID,
                        android.provider.ContactsContract.PhoneLookup.DISPLAY_NAME
                },
                null,
                null,
                null);

        if (contactLookup != null && contactLookup.getCount() > 0) {
            contactLookup.moveToNext();
            String name = contactLookup.getString(
                    contactLookup.getColumnIndex(android.provider.ContactsContract.Data.DISPLAY_NAME));
            if (name != null && name.length() > 0) {
                result = name;
            }
        }
    } catch (SecurityException se) {
        android.util.Log.w("F.Util.contactLookup", "READ_CONTACTS permission denied, returning number", se);
    } finally {
        if (contactLookup != null) {
            contactLookup.close();
        }
    }

    android.util.Log.d("F.Util.contactLookup", "Contact lookup result: " + result);
    return result;
}