public static BufferedImage getScreenshot( final Rectangle bounds ) {
	if ( bounds == null || bounds.width <= 0 || bounds.height <= 0 )
		return null;

	final HWND desktopWindow = USER.GetDesktopWindow();
	final HDC windowDC = GDI.GetDC( desktopWindow );
	if ( windowDC == null )
		return null;

	HBITMAP outputBitmap = null;
	HDC blitDC = null;
	try {
		outputBitmap = GDI.CreateCompatibleBitmap( windowDC, bounds.width, bounds.height );
		if ( outputBitmap == null )
			return null;

		blitDC = GDI.CreateCompatibleDC( windowDC );
		if ( blitDC == null )
			return null;

		final HANDLE oldBitmap = GDI.SelectObject( blitDC, outputBitmap );
		try {
			GDI.BitBlt( blitDC, 0, 0, bounds.width, bounds.height, windowDC, bounds.x, bounds.y, GDI32.SRCCOPY );
		} finally {
			if ( oldBitmap != null )
				GDI.SelectObject( blitDC, oldBitmap );
		}

		final BITMAPINFO bi = new BITMAPINFO( 40 );
		bi.bmiHeader.biSize = 40;

		final boolean ok = GDI.GetDIBits( blitDC, outputBitmap, 0, bounds.height, (byte[]) null, bi, WinGDI.DIB_RGB_COLORS );
		if ( !ok )
			return null;

		final BITMAPINFOHEADER bih = bi.bmiHeader;
		bih.biHeight = -Math.abs( bih.biHeight );
		bih.biCompression = 0;

		return bufferedImageFromBitmap( blitDC, outputBitmap, bi );
	} finally {
		if ( blitDC != null )
			GDI.DeleteDC( blitDC );
		if ( outputBitmap != null )
			GDI.DeleteObject( outputBitmap );
		USER.ReleaseDC( desktopWindow, windowDC );
	}
}