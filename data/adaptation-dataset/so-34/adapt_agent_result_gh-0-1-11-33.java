public static void main(String[] params) {

        // Old demo input (commented out for reference):
        // byte[] barray= new byte[]{ 0x01, 0x02, 0x04, 0x08,
        //                            0x10, 0x20, 0x40, (byte)0x80 };

        // New demo input:
        // 15-byte array, all zeros except a single 0x20 byte at index 5
        // Byte index:  0  1  2  3  4  [5] 6  7  8  9 10 11 12 13 14
        // Bit layout:  all bits cleared except bit 5 (0x20) in byte index 5
        byte[] barray = new byte[15];
        barray[5] = 0x20;

        BitSet bits = new BitSet();

        if (barray != null) {
            for (int i = 0; i < barray.length * 8; i++) {
                if ((barray[barray.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
                    bits.set(i);
                }
            }
        }
        System.out.println(bits);
    }