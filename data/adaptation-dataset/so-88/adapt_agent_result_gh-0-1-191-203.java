  // Reference: Adapted from a Stack Overflow answer on fast BufferedImage pixel copying using DataBufferInt.
  private static void copySrcIntoDstAt(final BufferedImage src,
                                       final BufferedImage dst,
                                       final int dx,
                                       final int dy) {
    if (src == null || dst == null) {
      throw new IllegalArgumentException("Source and destination images must not be null");
    }

    if (!(src.getRaster().getDataBuffer() instanceof DataBufferInt) ||
        !(dst.getRaster().getDataBuffer() instanceof DataBufferInt)) {
      throw new IllegalArgumentException("Both images must use DataBufferInt");
    }

    final int srcWidth  = src.getWidth();
    final int srcHeight = src.getHeight();
    final int dstWidth  = dst.getWidth();
    final int dstHeight = dst.getHeight();

    if (dx < 0 || dy < 0 || dx + srcWidth > dstWidth || dy + srcHeight > dstHeight) {
      throw new IllegalArgumentException("Source image does not fit within destination at the given offset");
    }

    final int[] srcbuf = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
    final int[] dstbuf = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();

    int srcoffs = 0;
    int dstoffs = dx + dy * dstWidth;

    for (int y = 0; y < srcHeight; y++, srcoffs += srcWidth, dstoffs += dstWidth) {
      System.arraycopy(srcbuf, srcoffs, dstbuf, dstoffs, srcWidth);
    }
  }