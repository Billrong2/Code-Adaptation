public List<UUID> parseUUIDs() {
    final List<UUID> uuids = new ArrayList<UUID>();
    if (bytes == null || bytes.length < 2) {
        return uuids;
    }

    int offset = 0;
    while (offset < (bytes.length - 2)) {
        int len = bytes[offset++];
        if (len <= 0) {
            break;
        }
        if (offset >= bytes.length) {
            break;
        }

        int type = bytes[offset++];
        switch (type) {
            case 0x02: // Partial list of 16-bit UUIDs
            case 0x03: // Complete list of 16-bit UUIDs
                while (len > 1 && offset + 1 < bytes.length) {
                    int uuid16 = bytes[offset++];
                    uuid16 += (bytes[offset++] << 8);
                    len -= 2;
                    uuids.add(UUID.fromString(String.format(
                            "%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                }
                break;
            case 0x06: // Partial list of 128-bit UUIDs
            case 0x07: // Complete list of 128-bit UUIDs
                // Loop through the advertised 128-bit UUIDs.
                while (len >= 16 && offset < bytes.length) {
                    try {
                        ByteBuffer buffer = ByteBuffer.wrap(bytes, offset++, 16)
                                .order(ByteOrder.LITTLE_ENDIAN);
                        long mostSignificantBit = buffer.getLong();
                        long leastSignificantBit = buffer.getLong();
                        uuids.add(new UUID(leastSignificantBit, mostSignificantBit));
                    } catch (IndexOutOfBoundsException e) {
                        // Suppressed: defensive programming without logging
                    } finally {
                        // Move the offset to read the next uuid.
                        offset += 15;
                        len -= 16;
                    }
                }
                break;
            default:
                // Skip over this data structure
                if (len > 0) {
                    offset += (len - 1);
                }
                break;
        }
    }

    return uuids;
}