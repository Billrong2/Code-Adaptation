private static byte[] createFakeSms(Context context, String sender, String body) {
    // Context is intentionally unused; method now only builds and returns an SMS PDU
    if (sender == null || body == null) {
        return null;
    }

    final String SERVICE_CENTER_NUMBER = "0000000000";
    final int SMS_DELIVER_PDU_TYPE = 0x04;
    final byte GSM_7BIT_ENCODING = 0x00;

    try {
        byte[] scBytes = android.telephony.PhoneNumberUtils
                .networkPortionToCalledPartyBCD(SERVICE_CENTER_NUMBER);
        byte[] senderBytes = android.telephony.PhoneNumberUtils
                .networkPortionToCalledPartyBCD(sender);

        int lsmcs = scBytes != null ? scBytes.length : 0;

        byte[] dateBytes = new byte[7];
        java.util.Calendar calendar = new java.util.GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(java.util.Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(java.util.Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(java.util.Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(java.util.Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(java.util.Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(java.util.Calendar.SECOND)));
        int tzQuarters = (calendar.get(java.util.Calendar.ZONE_OFFSET)
                + calendar.get(java.util.Calendar.DST_OFFSET)) / (60 * 1000 * 15);
        dateBytes[6] = reverseByte((byte) tzQuarters);

        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            outputStream.write(lsmcs);
            if (scBytes != null) {
                outputStream.write(scBytes);
            }
            outputStream.write(SMS_DELIVER_PDU_TYPE);
            outputStream.write((byte) sender.length());
            if (senderBytes != null) {
                outputStream.write(senderBytes);
            }
            outputStream.write(0x00); // PID
            outputStream.write(GSM_7BIT_ENCODING); // DCS: default 7-bit encoding
            outputStream.write(dateBytes);

            try {
                String reflectedClassName = "com.android.internal.telephony.GsmAlphabet";
                Class<?> gsmAlphabetClass = Class.forName(reflectedClassName);
                java.lang.reflect.Method stringToGsm7BitPacked = gsmAlphabetClass
                        .getMethod("stringToGsm7BitPacked", String.class);
                stringToGsm7BitPacked.setAccessible(true);
                byte[] bodyBytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
                if (bodyBytes != null) {
                    outputStream.write(bodyBytes);
                }
            } catch (Exception reflectionException) {
                android.util.Log.w(TAG, "Failed to encode SMS body", reflectionException);
                return null;
            }

            return outputStream.toByteArray();
        }
    } catch (Exception e) {
        android.util.Log.w(TAG, "Failed to build SMS PDU", e);
        return null;
    }
}