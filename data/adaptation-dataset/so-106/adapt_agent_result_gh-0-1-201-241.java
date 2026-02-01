@Override public void open(AudioInputStream stream) throws IOException, LineUnavailableException {
    if (stream == null) {
      throw new IllegalArgumentException("AudioInputStream must not be null");
    }

    AudioInputStream is1;
    format = stream.getFormat();
    if (format == null) {
      throw new IllegalArgumentException("AudioFormat must not be null");
    }

    // Ensure PCM_SIGNED encoding
    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
      is1 = AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, stream);
      if (is1 == null) {
        throw new IOException("Failed to convert AudioInputStream to PCM_SIGNED");
      }
    } else {
      is1 = stream;
    }

    format = is1.getFormat();

    // No UI/progress dependencies; read directly from the AudioInputStream
    AudioInputStream is2 = is1;

    final int BUFFER_SIZE = (1 << 16); // 64 KiB
    byte[] buffer = new byte[BUFFER_SIZE];

    try (AudioInputStream ais = is2; ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      int bytesRead;
      while ((bytesRead = ais.read(buffer, 0, buffer.length)) != -1) {
        if (bytesRead > 0) {
          baos.write(buffer, 0, bytesRead);
        }
      }
      audioData = baos.toByteArray();
    }

    if (audioData == null || audioData.length == 0) {
      throw new IOException("No audio data read from stream");
    }

    // Preserve existing channel-expansion logic
    AudioFormat afTemp;
    int frameSize = format.getFrameSize();
    if (format.getChannels() < 2) {
      afTemp = new AudioFormat(
        format.getEncoding(),
        format.getSampleRate(),
        format.getSampleSizeInBits(),
        2,
        (format.getSampleSizeInBits() * 2) / 8,
        format.getFrameRate(),
        format.isBigEndian()
      );
      frameSize = afTemp.getFrameSize();
    } else {
      afTemp = format;
    }

    // Initialize loop points only after validating audio data
    setLoopPoints(0, audioData.length);

    dataLine = AudioSystem.getSourceDataLine(afTemp);
    if (dataLine == null) {
      throw new LineUnavailableException("Failed to obtain SourceDataLine for format: " + afTemp);
    }
    dataLine.open();

    inputStream = new ByteArrayInputStream(audioData);
  }