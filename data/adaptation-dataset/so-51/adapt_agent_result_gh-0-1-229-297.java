public static byte[] convert(byte[] sourceBytes, javax.sound.sampled.AudioFormat audioFormat) {
    if (sourceBytes == null || sourceBytes.length == 0 || audioFormat == null) {
        throw new IllegalArgumentException("Illegal Argument passed to this method");
    }

    java.io.ByteArrayInputStream bais = null;
    java.io.ByteArrayOutputStream baos = null;
    javax.sound.sampled.AudioInputStream sourceAIS = null;
    javax.sound.sampled.AudioInputStream convert1AIS = null;
    javax.sound.sampled.AudioInputStream convert2AIS = null;

    try {
        bais = new java.io.ByteArrayInputStream(sourceBytes);
        sourceAIS = javax.sound.sampled.AudioSystem.getAudioInputStream(bais);
        javax.sound.sampled.AudioFormat sourceFormat = sourceAIS.getFormat();
        javax.sound.sampled.AudioFormat convertFormat = new javax.sound.sampled.AudioFormat(
                javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED,
                sourceFormat.getSampleRate(),
                16,
                sourceFormat.getChannels(),
                sourceFormat.getChannels() * 2,
                sourceFormat.getSampleRate(),
                false);

        convert1AIS = javax.sound.sampled.AudioSystem.getAudioInputStream(convertFormat, sourceAIS);
        convert2AIS = javax.sound.sampled.AudioSystem.getAudioInputStream(audioFormat, convert1AIS);

        baos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        while (true) {
            int readCount = convert2AIS.read(buffer, 0, buffer.length);
            if (readCount == -1) {
                break;
            }
            baos.write(buffer, 0, readCount);
        }
        return baos.toByteArray();
    } catch (Exception e) {
        try {
            org.myrobotlab.logging.Logging.logError(e);
        } catch (Exception ignore) {
            // guard against logging failures
        }
        return null;
    } finally {
        if (baos != null) {
            try {
                baos.close();
            } catch (Exception e) {
            }
        }
        if (convert2AIS != null) {
            try {
                convert2AIS.close();
            } catch (Exception e) {
            }
        }
        if (convert1AIS != null) {
            try {
                convert1AIS.close();
            } catch (Exception e) {
            }
        }
        if (sourceAIS != null) {
            try {
                sourceAIS.close();
            } catch (Exception e) {
            }
        }
        if (bais != null) {
            try {
                bais.close();
            } catch (Exception e) {
            }
        }
    }
}