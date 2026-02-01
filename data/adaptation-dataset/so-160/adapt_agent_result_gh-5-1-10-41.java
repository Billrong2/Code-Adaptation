public static float getCPUUsage() {
	final String STAT_PATH = "/proc/stat";
	final int SLEEP_MS = 360;
	final int IDLE_INDEX = 5;
	final int[] BUSY_INDICES = new int[] { 2, 3, 4, 6, 7, 8 };

	try (java.io.RandomAccessFile reader = new java.io.RandomAccessFile(STAT_PATH, "r")) {
		String statLine1 = reader.readLine();
		if (statLine1 == null) return 0f;
		String[] fields1 = statLine1.split(" "); // split on a single space
		if (fields1.length <= 8) return 0f;

		long idle1 = Long.parseLong(fields1[IDLE_INDEX]);
		long cpu1 = 0;
		for (int idx : BUSY_INDICES) cpu1 += Long.parseLong(fields1[idx]);

		try {
			Thread.sleep(SLEEP_MS);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
			return 0f;
		}

		reader.seek(0);
		String statLine2 = reader.readLine();
		if (statLine2 == null) return 0f;
		String[] fields2 = statLine2.split(" ");
		if (fields2.length <= 8) return 0f;

		long idle2 = Long.parseLong(fields2[IDLE_INDEX]);
		long cpu2 = 0;
		for (int idx : BUSY_INDICES) cpu2 += Long.parseLong(fields2[idx]);

		long denom = (cpu2 + idle2) - (cpu1 + idle1);
		if (denom == 0) return 0f;

		return (float) (cpu2 - cpu1) / (float) denom;
	} catch (java.io.IOException ex) {
		ex.printStackTrace();
		return 0f;
	}
}