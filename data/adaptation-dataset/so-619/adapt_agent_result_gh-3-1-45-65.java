public static List<AdRecord> parseScanRecord(byte[] scanRecord) {
    List<AdRecord> records = new ArrayList<>();

    if (scanRecord == null || scanRecord.length == 0) {
        return records;
    }

    int offset = 0;
    while (offset < scanRecord.length) {
        int length = scanRecord[offset++];
        // Done once we run out of records
        if (length == 0) {
            break;
        }

        // Ensure length is positive and within remaining bounds
        if (length < 0 || offset + length > scanRecord.length) {
            break;
        }

        int type = scanRecord[offset];
        // Done if our record isn't a valid type
        if (type == 0) {
            break;
        }

        byte[] data = Arrays.copyOfRange(scanRecord, offset + 1, offset + length);
        records.add(new AdRecord(length, type, data));

        // Advance
        offset += length;
    }

    return records;
}