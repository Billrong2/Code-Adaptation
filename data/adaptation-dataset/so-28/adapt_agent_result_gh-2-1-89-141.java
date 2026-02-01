private BufferedImage bufferedImageFromBitmap(final HDC dc, final HBITMAP bmp) {
	// Assumes dc, bmp and bi are managed/owned by the caller; no GDI lifecycle here.
	if (dc == null || bmp == null || bi == null || bi.bmiHeader == null)
		return null;

	final BITMAPINFOHEADER bih = bi.bmiHeader;
	final int width  = bih.biWidth;
	final int height = Math.abs(bih.biHeight);
	final int bitCount = bih.biBitCount;

	if (width <= 0 || height <= 0)
		return null;

	// Support only 16-bit and 32-bit formats
	final boolean is32 = bitCount == 32;
	final boolean is16 = bitCount == 16;
	if (!is32 && !is16)
		throw new IllegalArgumentException("Unsupported bit depth: " + bitCount);

	// DWORD-aligned stride (bytes per row)
	final int bytesPerPixel = is32 ? 4 : 2;
	final int strideBytes = ((width * bytesPerPixel + 3) / 4) * 4;

	// (Re)initialize cached objects if needed
	final boolean recreate = resultBufferedImage == null
			|| resultBufferedImage.getWidth() != width
			|| resultBufferedImage.getHeight() != height
			|| (is32 && !(buffer instanceof DataBufferInt))
			|| (is16 && !(buffer instanceof DataBufferUShort));

	if (recreate) {
		// Color masks
		final int rMask, gMask, bMask;
		if (is32) {
			rMask = 0x00FF0000;
			gMask = 0x0000FF00;
			bMask = 0x000000FF;
			cm = new DirectColorModel(32, rMask, gMask, bMask);
			final int[] data = new int[(strideBytes / 4) * height];
			buffer = new DataBufferInt(data, data.length);
		} else {
			// 16-bit: assume 5-6-5
			rMask = 0xF800;
			gMask = 0x07E0;
			bMask = 0x001F;
			cm = new DirectColorModel(16, rMask, gMask, bMask);
			final short[] data = new short[(strideBytes / 2) * height];
			buffer = new DataBufferUShort(data, data.length);
		}

		final int[] bandMasks = new int[] { rMask, gMask, bMask };
		raster = Raster.createPackedRaster(buffer, width, height, strideBytes / bytesPerPixel, bandMasks, null);
		resultBufferedImage = new BufferedImage(cm, raster, false, null);
	}

	// Fill pixel buffer directly via GetDIBits
	final boolean ok;
	if (is32) {
		ok = GDI.GetDIBits(dc, bmp, 0, height, ((DataBufferInt) buffer).getData(), bi, WinGDI.DIB_RGB_COLORS);
	} else {
		ok = GDI.GetDIBits(dc, bmp, 0, height, ((DataBufferUShort) buffer).getData(), bi, WinGDI.DIB_RGB_COLORS);
	}

	return ok ? resultBufferedImage : null;
}