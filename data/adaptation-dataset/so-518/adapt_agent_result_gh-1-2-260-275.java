public boolean setupAacDecoder(final int sampleRate, final int channels, final int bitRateKbps) throws java.io.IOException, android.media.MediaCodec.CodecException {
        if (sampleRate <= 0) {
            throw new IllegalArgumentException("sampleRate must be > 0");
        }
        if (channels <= 0) {
            throw new IllegalArgumentException("channels must be > 0");
        }
        if (bitRateKbps <= 0) {
            throw new IllegalArgumentException("bitRateKbps must be > 0");
        }

        if (decoder != null) {
            try {
                decoder.stop();
            } catch (Exception ignored) {
            }
            decoder.release();
            decoder = null;
        }

        decoder = android.media.MediaCodec.createDecoderByType("audio/mp4a-latm");
        android.media.MediaFormat format = new android.media.MediaFormat();
        format.setString(android.media.MediaFormat.KEY_MIME, "audio/mp4a-latm");
        format.setInteger(android.media.MediaFormat.KEY_SAMPLE_RATE, sampleRate);
        format.setInteger(android.media.MediaFormat.KEY_CHANNEL_COUNT, channels);
        format.setInteger(android.media.MediaFormat.KEY_BIT_RATE, bitRateKbps * 1024);
        format.setInteger(android.media.MediaFormat.KEY_AAC_PROFILE, android.media.MediaCodecInfo.CodecProfileLevel.AACObjectHE);

        decoder.configure(format, null, null, 0);

        return setPlayer(sampleRate);
    }