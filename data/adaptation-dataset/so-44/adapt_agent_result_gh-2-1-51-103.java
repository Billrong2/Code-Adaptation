private static void createFakeSms(final android.content.Context context, final String sender, final String body) {
    // Adapted from Stack Overflow answer on constructing fake SMS PDUs (see original SO thread).
    // Cosmetic updates, added validation, and clearer structure; core logic unchanged.

    if (context == null || sender == null || body == null) {
        return; // hardening: avoid NPEs
    }

    // basic validation to avoid malformed PDUs
    if (sender.length() == 0 || sender.length() > 20) {
        return;
    }

    byte[] pdu = null;

    final byte ENCODING_7BIT = 0x00;
    final byte SMS_DELIVER = 0x04;

    final byte[] serviceCenterBytes = android.telephony.PhoneNumberUtils
            .networkPortionToCalledPartyBCD("0000000000");
    final byte[] senderBytes = android.telephony.PhoneNumberUtils
            .networkPortionToCalledPartyBCD(sender);

    final int serviceCenterLength = serviceCenterBytes.length;

    final byte[] dateBytes = new byte[7];
    final java.util.Calendar calendar = new java.util.GregorianCalendar();

    dateBytes[0] = reverseByte((byte) (calendar.get(java.util.Calendar.YEAR)));
    dateBytes[1] = reverseByte((byte) (calendar.get(java.util.Calendar.MONTH) + 1));
    dateBytes[2] = reverseByte((byte) (calendar.get(java.util.Calendar.DAY_OF_MONTH)));
    dateBytes[3] = reverseByte((byte) (calendar.get(java.util.Calendar.HOUR_OF_DAY)));
    dateBytes[4] = reverseByte((byte) (calendar.get(java.util.Calendar.MINUTE)));
    dateBytes[5] = reverseByte((byte) (calendar.get(java.util.Calendar.SECOND)));
    dateBytes[6] = reverseByte((byte) ((calendar.get(java.util.Calendar.ZONE_OFFSET)
            + calendar.get(java.util.Calendar.DST_OFFSET)) / (60 * 1000 * 15)));

    try (java.io.ByteArrayOutputStream byteStream = new java.io.ByteArrayOutputStream()) {
        byteStream.write(serviceCenterLength);
        byteStream.write(serviceCenterBytes);
        byteStream.write(SMS_DELIVER);
        byteStream.write((byte) sender.length());
        byteStream.write(senderBytes);
        byteStream.write(0x00);
        byteStream.write(ENCODING_7BIT); // default 7-bit encoding
        byteStream.write(dateBytes);

        try {
            // Reflection into internal Android API as per original SO solution
            final String reflectedClassName = "com.android.internal.telephony.GsmAlphabet";
            final java.lang.Class<?> gsmAlphabetClass = java.lang.Class.forName(reflectedClassName);
            final java.lang.reflect.Method stringToGsm7BitPacked = gsmAlphabetClass.getMethod(
                    "stringToGsm7BitPacked", new java.lang.Class<?>[]{String.class});
            stringToGsm7BitPacked.setAccessible(true);

            final byte[] bodyBytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
            byteStream.write(bodyBytes);
        } catch (java.lang.ClassNotFoundException
                | java.lang.NoSuchMethodException
                | java.lang.IllegalAccessException
                | java.lang.reflect.InvocationTargetException reflectionException) {
            android.util.Log.w("SMSSpoof", "Reflection failed while encoding SMS body", reflectionException);
            return;
        }

        pdu = byteStream.toByteArray();
    } catch (java.io.IOException ioException) {
        android.util.Log.w("SMSSpoof", "I/O error while building SMS PDU", ioException);
        return;
    }

    final android.content.Intent intent = new android.content.Intent();
    intent.setClassName("com.android.mms",
            "com.android.mms.transaction.SmsReceiverService");
    intent.setAction("android.provider.Telephony.SMS_RECEIVED");
    intent.putExtra("pdus", new java.lang.Object[]{pdu});
    intent.putExtra("format", "3gpp");

    context.startService(intent);
}