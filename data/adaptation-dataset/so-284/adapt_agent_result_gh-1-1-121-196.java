public BOMStripperInputStream(final InputStream inputStream) throws NullPointerException, IOException
{
  if (inputStream == null)
    throw new NullPointerException("invalid input stream: null is not allowed");

  final int PUSHBACK_BUFFER_SIZE = 4; // sufficient to hold the largest known BOM

  in = new PushbackInputStream(inputStream, PUSHBACK_BUFFER_SIZE);

  final byte[] bomBytes = new byte[PUSHBACK_BUFFER_SIZE];
  final int read = in.read(bomBytes);

  switch (read)
  {
    case 4:
      if ((bomBytes[0] == (byte)0xFF) &&
          (bomBytes[1] == (byte)0xFE) &&
          (bomBytes[2] == (byte)0x00) &&
          (bomBytes[3] == (byte)0x00))
      {
        this.bom = BOM.UTF_32_LE;
        break;
      }
      else if ((bomBytes[0] == (byte)0x00) &&
               (bomBytes[1] == (byte)0x00) &&
               (bomBytes[2] == (byte)0xFE) &&
               (bomBytes[3] == (byte)0xFF))
      {
        this.bom = BOM.UTF_32_BE;
        break;
      }

    case 3:
      if ((bomBytes[0] == (byte)0xEF) &&
          (bomBytes[1] == (byte)0xBB) &&
          (bomBytes[2] == (byte)0xBF))
      {
        this.bom = BOM.UTF_8;
        break;
      }

    case 2:
      if ((bomBytes[0] == (byte)0xFF) &&
          (bomBytes[1] == (byte)0xFE))
      {
        this.bom = BOM.UTF_16_LE;
        break;
      }
      else if ((bomBytes[0] == (byte)0xFE) &&
               (bomBytes[1] == (byte)0xFF))
      {
        this.bom = BOM.UTF_16_BE;
        break;
      }

    default:
      this.bom = BOM.NONE;
      break;
  }

  if (read > 0)
    in.unread(bomBytes, 0, read);
}