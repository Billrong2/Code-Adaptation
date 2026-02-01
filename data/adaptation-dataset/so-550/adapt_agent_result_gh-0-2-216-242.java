private int initAudioRecord(int rate)
{
    if (rate <= 0)
    {
        Log.v("AudioRecord", "Invalid sample rate: " + rate);
        return -1;
    }

    // Release any existing recorder before reinitialization
    try
    {
        if (recorder != null)
        {
            recorder.release();
            recorder = null;
        }
    }
    catch (Exception e)
    {
        Log.v("AudioRecord", "Error releasing previous recorder", e);
    }

    try
    {
        Log.v("AudioRecord", "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
        bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

        if (bufferSize <= 0 || bufferSize == AudioRecord.ERROR_BAD_VALUE)
        {
            Log.v("AudioRecord", "Invalid buffer size for rate " + rate + ": " + bufferSize);
            return -1;
        }

        recorder = new AudioRecord(AudioSource.MIC, rate, channelConfig, audioFormat, bufferSize);

        if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
        {
            Log.v("AudioRecord", "AudioRecord initialized at " + rate + "Hz, bits: " + audioFormat + ", channel: " + channelConfig);
            return rate;
        }
        else
        {
            Log.v("AudioRecord", "AudioRecord failed to initialize at rate " + rate);
            return -1;
        }
    }
    catch (IllegalArgumentException e)
    {
        Log.v("AudioRecord", "Illegal argument while initializing AudioRecord at rate " + rate, e);
        return -1;
    }
    catch (Exception e)
    {
        Log.v("AudioRecord", "Unexpected error while initializing AudioRecord at rate " + rate, e);
        return -1;
    }
}