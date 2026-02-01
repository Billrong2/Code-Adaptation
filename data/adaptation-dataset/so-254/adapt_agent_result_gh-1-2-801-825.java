public static void tone(int hz, int msecs, double vol) {
    javax.sound.sampled.SourceDataLine sdl = null;
    try {
      byte[] buf = new byte[1];
      javax.sound.sampled.AudioFormat af = new javax.sound.sampled.AudioFormat(
          SAMPLE_RATE, // sampleRate
          8,           // sampleSizeInBits
          1,           // channels
          true,        // signed
          false);      // bigEndian
      sdl = javax.sound.sampled.AudioSystem.getSourceDataLine(af);
      sdl.open(af);
      sdl.start();
      for (int i = 0; i < msecs * 8; i++) {
        double angle = i / (SAMPLE_RATE / hz) * 2.0 * Math.PI;
        buf[0] = (byte) (Math.sin(angle) * 127.0 * vol);
        sdl.write(buf, 0, 1);
      }
      sdl.drain();
    } catch (Exception e) {
      // fail quietly
    } finally {
      try {
        if (sdl != null) {
          sdl.stop();
          sdl.close();
        }
      } catch (Exception e) {
        // fail quietly
      }
    }
  }