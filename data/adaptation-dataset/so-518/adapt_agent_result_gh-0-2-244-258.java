public boolean initAacEncoder(int sampleRate, int channels, int bitRate) throws Exception {
        if (sampleRate <= 0 || channels <= 0 || bitRate <= 0) {
            throw new IllegalArgumentException("Invalid audio parameters");
        }

        // Create and configure AAC encoder
        MediaFormat format = MediaFormat.createAudioFormat("audio/mp4a-latm", sampleRate, channels);
        format.setInteger(MediaFormat.KEY_BIT_RATE, bitRate * 1024);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectHE);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channels);
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);

        encoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
        encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        // Initialize audio capture separately
        initAudioRecord(sampleRate);

        return true;
    }