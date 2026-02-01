/**
 * Resolve the fully qualified class name from the given classfile bytes.
 * 
 * @param classBytes compiled classfile bytes
 * @return fully qualified class name
 * @throws Exception if the classfile cannot be parsed
 */
public static String getClassName(byte[] classBytes) throws Exception {
    if (classBytes == null || classBytes.length == 0) {
        throw new IllegalArgumentException("classBytes must not be null or empty");
    }

    try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(classBytes))) {
        dis.readLong(); // skip header and class version
        int cpcnt = (dis.readShort() & 0xffff) - 1;
        int[] classes = new int[cpcnt];
        String[] strings = new String[cpcnt];
        for (int i = 0; i < cpcnt; i++) {
            int t = dis.read();
            if (t == 7) {
                classes[i] = dis.readShort() & 0xffff;
            } else if (t == 1) {
                strings[i] = dis.readUTF();
            } else if (t == 5 || t == 6) {
                dis.readLong();
                i++;
            } else if (t == 8) {
                dis.readShort();
            } else {
                dis.readInt();
            }
        }
        dis.readShort(); // skip access flags
        return strings[classes[(dis.readShort() & 0xffff) - 1] - 1].replace('/', '.');
    }
}