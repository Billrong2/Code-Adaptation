public boolean configureDecoder(int sampleRate, int channels, int bitRate) throws Exception {
        if (sampleRate <= 0 || channels <= 0 || bitRate <= 0) {
            throw new IllegalArgumentException("Invalid audio parameters");
        }

        android.media.MediaCodec localDecoder = null;
        try {
            android.media.MediaFormat format = android.media.MediaFormat.createAudioFormat(
                    "audio/mp4a-latm", sampleRate, channels);
            format.setInteger(android.media.MediaFormat.KEY_BIT_RATE, bitRate * 1024);
            format.setInteger(android.media.MediaFormat.KEY_AAC_PROFILE,
                    android.media.MediaCodecInfo.CodecProfileLevel.AACObjectHE);

            localDecoder = android.media.MediaCodec.createDecoderByType("audio/mp4a-latm");
            localDecoder.configure(format, null, null, 0);
            localDecoder.start();

            // prepare audio output for playback
            if (!setPlayer(sampleRate)) {
                throw new IllegalStateException("AudioTrack player initialization failed");
            }

            // assign only after successful configuration
            this.decoder = localDecoder;
            return true;
        } catch (Exception e) {
            if (localDecoder != null) {
                try {
                    localDecoder.stop();
                } catch (Exception ignored) {
                }
                try {
                    localDecoder.release();
                } catch (Exception ignored) {
                }
            }
            throw e;
        }
    }