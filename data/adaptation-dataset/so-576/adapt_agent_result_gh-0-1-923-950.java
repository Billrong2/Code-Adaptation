public static String encode(final byte[] buf)
{
	/**
	 * Translates the specified byte array into a Base64 string.
	 *
	 * @param buf the byte array (not null)
	 * @return the translated Base64 string (not null)
	 */
	final int size = buf.length;
	final char[] out = new char[((size + 2) / 3) * 4];

	final int sixBitMask = 0x3F;
	int outIndex = 0;
	int i = 0;

	while (i < size)
	{
		final byte b0 = buf[i++];
		final byte b1 = (i < size) ? buf[i++] : 0;
		final byte b2 = (i < size) ? buf[i++] : 0;

		out[outIndex++] = ALPHABET[(b0 >> 2) & sixBitMask];
		out[outIndex++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & sixBitMask];
		out[outIndex++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & sixBitMask];
		out[outIndex++] = ALPHABET[b2 & sixBitMask];
	}

	switch (size % 3)
	{
		case 1:
			out[--outIndex] = '=';
		case 2:
			out[--outIndex] = '=';
		default:
			break;
	}

	return new String(out);
}