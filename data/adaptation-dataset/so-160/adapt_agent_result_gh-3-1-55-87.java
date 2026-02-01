public static float getCPUUtil() {
    try {
        long idle1;
        long cpu1;
        long idle2;
        long cpu2;
        String load;
        String[] tokens;

        try (java.io.RandomAccessFile reader = new java.io.RandomAccessFile("/proc/stat", "r")) {
            load = reader.readLine();
            if (load == null) {
                return 0f;
            }
            tokens = load.split(" ");
            if (tokens.length <= 8) {
                return 0f;
            }
            idle1 = Long.parseLong(tokens[5]);
            cpu1 = Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) + Long.parseLong(tokens[4])
                    + Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]) + Long.parseLong(tokens[8]);

            try {
                Thread.sleep(360);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }

            reader.seek(0);
            load = reader.readLine();
            if (load == null) {
                return 0f;
            }
            tokens = load.split(" ");
            if (tokens.length <= 8) {
                return 0f;
            }
            idle2 = Long.parseLong(tokens[5]);
            cpu2 = Long.parseLong(tokens[2]) + Long.parseLong(tokens[3]) + Long.parseLong(tokens[4])
                    + Long.parseLong(tokens[6]) + Long.parseLong(tokens[7]) + Long.parseLong(tokens[8]);
        }

        return (float) (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));
    } catch (java.io.IOException e) {
        e.printStackTrace();
    } catch (NumberFormatException e) {
        e.printStackTrace();
    }
    return 0f;
}