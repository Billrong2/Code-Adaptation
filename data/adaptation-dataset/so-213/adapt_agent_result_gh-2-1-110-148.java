private List<UUID> parseUuids(final byte[] advertisedData) {
    // Workaround for Android 4.3/4.4 BLE scan filtering bug:
    // Some Android versions fail to correctly filter devices by advertised services,
    // so we manually parse the raw scan record to extract UUIDs.

    final List<UUID> uuids = new ArrayList<>();

    if (advertisedData == null || advertisedData.length < 2) {
        return uuids;
    }

    final String BASE_UUID_FORMAT = "%08x-0000-1000-8000-00805f9b34fb";

    final ByteBuffer buffer = ByteBuffer.wrap(advertisedData).order(ByteOrder.LITTLE_ENDIAN);
    try {
        while (buffer.remaining() > 2) {
            int length = buffer.get() & 0xFF;
            if (length == 0) {
                break;
            }

            // Length includes the type byte; ensure it does not exceed remaining bytes
            if (length > buffer.remaining()) {
                break; // malformed record, stop parsing
            }

            final byte type = buffer.get();
            switch (type) {
                case 0x02: // Partial list of 16-bit UUIDs
                case 0x03: // Complete list of 16-bit UUIDs
                    while (length >= 2 && buffer.remaining() >= 2) {
                        uuids.add(UUID.fromString(String.format(BASE_UUID_FORMAT, buffer.getShort())));
                        length -= 2;
                    }
                    break;

                case 0x06: // Partial list of 128-bit UUIDs
                case 0x07: // Complete list of 128-bit UUIDs
                    while (length >= 16 && buffer.remaining() >= 16) {
                        final long lsb = buffer.getLong();
                        final long msb = buffer.getLong();
                        uuids.add(new UUID(msb, lsb));
                        length -= 16;
                    }
                    break;

                default:
                    // Skip the remaining bytes for this record (length minus type byte)
                    buffer.position(buffer.position() + length - 1);
                    break;
            }
        }
    } catch (java.nio.BufferUnderflowException e) {
        // Defensive: ignore malformed scan records and return what we have parsed so far
    }

    return uuids;
}