private boolean isCompletelyWritten(File file) {
    // Attribution: Adapted from a StackOverflow answer (source ID placeholder)
    RandomAccessFile stream = null;
    try {
        stream = new RandomAccessFile(file, "rw");
        return true;
    } catch (Exception e) {
        android.util.Log.d("INFO", "Skipping file " + file.getName() + " for this iteration; it is not fully written yet");
    } finally {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                android.util.Log.d("INFO", "Exception encountered while closing file " + file.getName());
            }
        }
    }
    return false;
}