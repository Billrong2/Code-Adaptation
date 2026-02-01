private static BufferedImage bufferedImageFromBitmap( final HDC dc, final HBITMAP bitmap, final BITMAPINFO bi ) {
	// Validate inputs
	if ( dc == null || bitmap == null || bi == null || bi.bmiHeader == null )
		return null;

	final BITMAPINFOHEADER bih = bi.bmiHeader;
	final int bitCount = bih.biBitCount;
	if ( bitCount != 16 && bitCount != 32 )
		throw new IllegalArgumentException( "Unsupported bit depth: " + bitCount );

	final int width = bih.biWidth;
	final int height = Math.abs( bih.biHeight ); // orientation is controlled by the sign in biHeight
	if ( width <= 0 || height <= 0 )
		return null;

	final int bytesPerPixel = bitCount / 8;

	// Compute 32-bit aligned stride (in bytes)
	long rowBits = (long) width * (long) bitCount;		// may overflow int
	long alignedRowBits = ( rowBits + 31L ) & ~31L; // align to 32 bits
	long strideBytesLong = alignedRowBits / 8L;
	if ( strideBytesLong <= 0 || strideBytesLong > Integer.MAX_VALUE )
		return null;
	final int strideBytes = (int) strideBytesLong;

	// Compute scanline stride in pixels
	if ( strideBytes % bytesPerPixel != 0 )
		return null;
	final int scanlineStride = strideBytes / bytesPerPixel;

	// Allocate buffer sized to aligned stride * height
	long totalElementsLong = (long) scanlineStride * (long) height;
	if ( totalElementsLong <= 0 || totalElementsLong > Integer.MAX_VALUE )
		return null;

	final ColorModel colorModel;
	final WritableRaster raster;
	final boolean ok;

	if ( bitCount == 32 ) {
		final int[] pixels = new int[ (int) totalElementsLong ];
		ok = GDI.GetDIBits( dc, bitmap, 0, height, pixels, bi, 0 /* DIB_RGB_COLORS */ );
		if ( !ok )
			return null;

		// 8-8-8 RGB, ignore alpha
		colorModel = new DirectColorModel( 24, 0x00FF0000, 0x0000FF00, 0x000000FF );
		raster = Raster.createPackedRaster( new DataBufferInt( pixels, pixels.length ), width, height, scanlineStride,
				new int[] { 0x00FF0000, 0x0000FF00, 0x000000FF }, null );
	}
	else {
		final short[] pixels = new short[ (int) totalElementsLong ];
		ok = GDI.GetDIBits( dc, bitmap, 0, height, pixels, bi, 0 /* DIB_RGB_COLORS */ );
		if ( !ok )
			return null;

		// 5-5-5 RGB
		colorModel = new DirectColorModel( 15, 0x7C00, 0x03E0, 0x001F );
		raster = Raster.createPackedRaster( new DataBufferUShort( pixels, pixels.length ), width, height, scanlineStride,
				new int[] { 0x7C00, 0x03E0, 0x001F }, null );
	}

	return new BufferedImage( colorModel, raster, false, null );
}