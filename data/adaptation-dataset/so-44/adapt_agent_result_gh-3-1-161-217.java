private static byte[] createFakeSms2(Context context, String sender, String body) {
    if (sender == null || sender.length() == 0 || body == null) {
        Log.e(TAG, "createFakeSms2: sender or body is null/empty");
        return null;
    }

    // Service center address (dummy)
    byte[] scBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD("0000000000");
    byte[] senderBytes = PhoneNumberUtils.networkPortionToCalledPartyBCD(sender);
    int lsmcs = scBytes != null ? scBytes.length : 0;

    // Prepare timestamp
    Calendar calendar = new GregorianCalendar();
    byte[] dateBytes = new byte[7];
    dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
    dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
    dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
    dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
    dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
    dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
    dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));

    // Encoding selection
    byte[] userData = null;
    byte dcs;
    boolean useGsm7 = false;

    try {
        // Try GSM 7-bit via internal API
        Class<?> gsmAlphabet = Class.forName("com.android.internal.telephony.GsmAlphabet");
        Method stringToGsm7BitPacked = gsmAlphabet.getMethod("stringToGsm7BitPacked", String.class);
        userData = (byte[]) stringToGsm7BitPacked.invoke(null, body);
        if (userData != null) {
            useGsm7 = true;
            dcs = 0x00; // GSM 7-bit default
            Log.i(TAG, "createFakeSms2: using GSM 7-bit encoding");
        } else {
            throw new UnsupportedEncodingException("GSM 7-bit returned null");
        }
    } catch (Exception e) {
        // Fallback to UCS-2
        try {
            userData = encodeUCS2(body, null);
            dcs = 0x08; // UCS-2
            Log.i(TAG, "createFakeSms2: falling back to UCS-2 encoding");
        } catch (Exception ucs2e) {
            Log.e(TAG, "createFakeSms2: unsupported encoding for body", ucs2e);
            return null;
        }
    }

    // Build PDU
    try (ByteArrayOutputStream bo = new ByteArrayOutputStream()) {
        bo.write(lsmcs);
        if (scBytes != null) {
            bo.write(scBytes);
        }
        bo.write(0x04); // SMS-DELIVER
        bo.write((byte) sender.length());
        bo.write(senderBytes);
        bo.write(0x00); // PID
        bo.write(useGsm7 ? 0x00 : 0x08); // DCS after encoding decision
        bo.write(dateBytes); // Timestamp after encoding decision
        bo.write(userData);
        return bo.toByteArray();
    } catch (IOException ioe) {
        Log.e(TAG, "createFakeSms2: IO error while building PDU", ioe);
        return null;
    }
}