public void uncaughtException(Thread t, Throwable e) {
	// generate filename based on epoch milliseconds
	long epochMillis = new java.util.Date().getTime();
	String filename = epochMillis + ".stacktrace";

	// capture stacktrace (unchanged logic, but hardened resource handling)
	String stacktrace;
	java.io.StringWriter result = new java.io.StringWriter();
	try (java.io.PrintWriter printWriter = new java.io.PrintWriter(result)) {
		e.printStackTrace(printWriter);
		stacktrace = result.toString();
	}

	// send to server first, if configured
	if (url != null) {
		sendToServer(stacktrace, filename);
	}

	// write locally to SD card, if configured
	if (localPath != null) {
		try {
			net.krautchan.android.helpers.FileHelpers.writeToSDFile(stacktrace, filename, localPath);
		} catch (Exception ex) {
			// avoid introducing new unchecked exceptions during crash handling
			ex.printStackTrace();
		}
	}

	// delegate to default handler only if thread is non-null
	if (t != null && defaultUEH != null) {
		defaultUEH.uncaughtException(t, e);
	}
}