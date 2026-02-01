public UnicodeInputStream(final InputStream inputStream, final boolean skipBom) throws NullPointerException, IOException {
		if (inputStream == null) {
			throw new NullPointerException("invalid input stream: null is not allowed");
		}

		final int MAX_BOM_LENGTH = 4;
		this.in = new PushbackInputStream(inputStream, MAX_BOM_LENGTH);

		final byte[] probe = new byte[MAX_BOM_LENGTH];
		final int read = in.read(probe);

		if (read == -1) {
			this.bom = BOM.NONE;
			return;
		}

		switch (read) {
			case 4:
				if ((probe[0] == (byte) 0xFF) && (probe[1] == (byte) 0xFE) && (probe[2] == (byte) 0x00)
						&& (probe[3] == (byte) 0x00)) {
					this.bom = BOM.UTF_32_LE;
					break;
				} else if ((probe[0] == (byte) 0x00) && (probe[1] == (byte) 0x00)
						&& (probe[2] == (byte) 0xFE) && (probe[3] == (byte) 0xFF)) {
					this.bom = BOM.UTF_32_BE;
					break;
				}
			case 3:
				if ((probe[0] == (byte) 0xEF) && (probe[1] == (byte) 0xBB) && (probe[2] == (byte) 0xBF)) {
					this.bom = BOM.UTF_8;
					break;
				}
			case 2:
				if ((probe[0] == (byte) 0xFF) && (probe[1] == (byte) 0xFE)) {
					this.bom = BOM.UTF_16_LE;
					break;
				} else if ((probe[0] == (byte) 0xFE) && (probe[1] == (byte) 0xFF)) {
					this.bom = BOM.UTF_16_BE;
					break;
				}
			default:
				this.bom = BOM.NONE;
				break;
		}

		if (read > 0) {
			in.unread(probe, 0, read);
		}

		if (skipBom && this.bom != BOM.NONE) {
			final int toSkip = this.bom.bytes.length;
			long skipped = 0;
			while (skipped < toSkip) {
				final long s = in.skip(toSkip - skipped);
				if (s <= 0) {
					break;
				}
				skipped += s;
			}
		}
	}