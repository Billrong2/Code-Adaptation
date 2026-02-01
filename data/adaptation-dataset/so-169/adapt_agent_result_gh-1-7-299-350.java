private java.util.List<java.util.UUID> parseUUIDs(final byte[] advertisedData) {
    // Parses UUIDs from BLE advertisement data. Supports both 16-bit and 128-bit UUID entries.
    final java.util.List<java.util.UUID> uuids = new java.util.ArrayList<java.util.UUID>();

    int offset = 0;
    while (advertisedData != null && offset < (advertisedData.length - 2)) {
        int len = advertisedData[offset++];
        if (len == 0) {
            break;
        }

        int type = advertisedData[offset++];
        switch (type) {
            case 0x02: // Partial list of 16-bit UUIDs
            case 0x03: // Complete list of 16-bit UUIDs
                // Each UUID is encoded as 2 bytes, little-endian
                while (len > 1) {
                    int uuid16 = advertisedData[offset++];
                    uuid16 += (advertisedData[offset++] << 8);
                    len -= 2;
                    uuids.add(java.util.UUID.fromString(String.format(
                            "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                }
                break;

            case 0x06: // Partial list of 128-bit UUIDs
            case 0x07: // Complete list of 128-bit UUIDs
                // Loop through the advertised 128-bit UUIDs (16 bytes each)
                while (len >= 16) {
                    try {
                        // Wrap the advertised bytes and read them in little-endian order
                        java.nio.ByteBuffer buffer = java.nio.ByteBuffer
                                .wrap(advertisedData, offset++, 16)
                                .order(java.nio.ByteOrder.LITTLE_ENDIAN);
                        long mostSignificantBit = buffer.getLong();
                        long leastSignificantBit = buffer.getLong();
                        uuids.add(new java.util.UUID(leastSignificantBit, mostSignificantBit));
                    } catch (IndexOutOfBoundsException e) {
                        // Defensive programming: suppress logging here to avoid noisy logs
                        // and continue scanning remaining advertisement data.
                        continue;
                    } finally {
                        // Move the offset to the next UUID and update remaining length
                        offset += 15;
                        len -= 16;
                    }
                }
                break;

            default:
                // Skip over data we do not currently handle
                offset += (len - 1);
                break;
        }
    }

    return uuids;
}