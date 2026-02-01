public static final String readRegistry(final String location, final String key) {
	if (location == null || location.length() == 0 || key == null || key.length() == 0) {
		return null;
	}
	try {
		final Process process = Runtime.getRuntime().exec("reg query \"" + location + "\" /v " + key); //$NON-NLS-1$ //$NON-NLS-2$
		final StreamReader reader = new StreamReader(process.getInputStream());
		reader.start();
		process.waitFor();
		reader.join();
		final String output = reader.getResult();

		if (!output.contains("\t")) { //$NON-NLS-1$
			return null;
		}

		final String[] parsed = output.split("\t"); //$NON-NLS-1$
		return parsed[parsed.length - 1];
	} catch (Exception e) {
		return null;
	}
}