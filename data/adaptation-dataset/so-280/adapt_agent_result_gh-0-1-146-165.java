/**
	 * Reads a variable-length UTF-8 string.
	 * <p>
	 * Format:
	 * <ul>
	 *   <li>0x00 = empty string</li>
	 *   <li>0x0B &lt;length&gt; &lt;bytes&gt; = UTF-8 string, where length is ULEB128</li>
	 * </ul>
	 */
	public String readString() throws IOException {
		byte kind = readByte();
		if (kind == 0)
			return "";
		if (kind != 0x0B) {
			throw new IOException(String.format(
				"String format error: Expected 0x0B or 0x00, found 0x%02X",
				(kind & 0xFF)
			));
		}

		int length = readULEB128();
		if (length == 0)
			return "";

		byte[] utf8bytes = new byte[length];
		this.reader.readFully(utf8bytes);
		return new String(utf8bytes, "UTF-8");
	}