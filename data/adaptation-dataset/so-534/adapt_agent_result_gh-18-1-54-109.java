public UnicodeReader(InputStream in, Charset defaultCharset) throws IOException {
		if (in == null) {
			throw new NullPointerException("InputStream must not be null");
		}

		final byte[] bom = new byte[BOM_SIZE];
		final PushbackInputStream pushbackStream = new PushbackInputStream(in, BOM_SIZE);
		final int n = pushbackStream.read(bom, 0, bom.length);

		final Charset charset;
		final int unread;

		// Read ahead up to four bytes and check for BOM marks.
		if (n >= 3 && (bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
			charset = Charset.forName("UTF-8");
			unread = n - 3;
		} else if (n >= 2 && (bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
			charset = Charset.forName("UTF-16BE");
			unread = n - 2;
		} else if (n >= 2 && (bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
			charset = Charset.forName("UTF-16LE");
			unread = n - 2;
		} else if (n >= 4 && (bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE) && (bom[3] == (byte) 0xFF)) {
			charset = Charset.forName("UTF-32BE");
			unread = n - 4;
		} else if (n >= 4 && (bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00) && (bom[3] == (byte) 0x00)) {
			charset = Charset.forName("UTF-32LE");
			unread = n - 4;
		} else {
			charset = defaultCharset != null ? defaultCharset : Charset.defaultCharset();
			unread = n;
		}

		// Unread bytes if necessary and skip BOM marks.
		if (unread > 0) {
			pushbackStream.unread(bom, n - unread, unread);
		}

		this.reader = new InputStreamReader(pushbackStream, charset);
	}