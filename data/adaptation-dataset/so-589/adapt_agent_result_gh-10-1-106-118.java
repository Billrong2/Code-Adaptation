private String hexify(final byte[] bytes) {
	if (bytes == null) {
		return "";
	}

	final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	final StringBuilder buf = new StringBuilder(bytes.length * 2);

	for (int i = 0; i < bytes.length; ++i) {
		buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
		buf.append(hexDigits[bytes[i] & 0x0f]);
	}

	return buf.toString();
}