/**
	 * Converts a hexadecimal string into a byte array.
	 * <p>
	 * Odd-length strings are accepted and will be left-padded with a leading '0'.
	 * A {@link NullPointerException} will be thrown if the input is {@code null}.
	 * </p>
	 *
	 * @param s hex-encoded string
	 * @return decoded byte array
	 */
	static byte[] hexStringToByteArray(String s) {
		int len = s.length(); // may throw NullPointerException by design
		if ((len % 2) == 1) {
			s = "0" + s;
			len++;
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			int high = Character.digit(s.charAt(i), 16);
			int low = Character.digit(s.charAt(i + 1), 16);
			data[i / 2] = (byte) ((high << 4) + low);
		}
		return data;
	}