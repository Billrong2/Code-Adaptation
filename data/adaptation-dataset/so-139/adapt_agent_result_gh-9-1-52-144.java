public static String getRelativePath(String targetPath, String basePath, String pathSeparator) {
	// ---- code hardening: null/empty validation ----
	if (targetPath == null || basePath == null) {
		throw new PathResolutionException(java.util.ResourceBundle.getBundle("net.rptools.maptool.launcher.Messages")
				.getString("path.relative.null"));
	}
	if (pathSeparator == null || pathSeparator.length() != 1) {
		throw new PathResolutionException(java.util.ResourceBundle.getBundle("net.rptools.maptool.launcher.Messages")
				.getString("path.relative.badSeparator"));
	}

	final char separator = pathSeparator.charAt(0);
	if (separator != UNIX_SEPARATOR && separator != WINDOWS_SEPARATOR) {
		throw new PathResolutionException(java.util.ResourceBundle.getBundle("net.rptools.maptool.launcher.Messages")
				.getString("path.relative.badSeparator"));
	}

	// ---- normalize paths (no end separator) ----
	String normalizedTargetPath = normalizeNoEndSeparator(targetPath, separator == UNIX_SEPARATOR);
	String normalizedBasePath = normalizeNoEndSeparator(basePath, separator == UNIX_SEPARATOR);

	if (normalizedTargetPath == null || normalizedBasePath == null) {
		throw new PathResolutionException(java.util.ResourceBundle.getBundle("net.rptools.maptool.launcher.Messages")
				.getString("path.relative.normalizeFailed"));
	}

	// ---- unify separators explicitly ----
	if (separator == UNIX_SEPARATOR) {
		normalizedTargetPath = separatorsToUnix(normalizedTargetPath);
		normalizedBasePath = separatorsToUnix(normalizedBasePath);
	} else {
		normalizedTargetPath = separatorsToWindows(normalizedTargetPath);
		normalizedBasePath = separatorsToWindows(normalizedBasePath);
	}

	// ---- identical paths edge case ----
	if (normalizedTargetPath.equals(normalizedBasePath)) {
		return ".";
	}

	final String sepRegex = java.util.regex.Pattern.quote(String.valueOf(separator));
	final String[] targetSegments = normalizedTargetPath.split(sepRegex);
	final String[] baseSegments = normalizedBasePath.split(sepRegex);

	// ---- find common prefix ----
	StringBuilder commonBuilder = new StringBuilder();
	int commonIndex = 0;
	while (commonIndex < targetSegments.length && commonIndex < baseSegments.length
			&& targetSegments[commonIndex].equals(baseSegments[commonIndex])) {
		commonBuilder.append(targetSegments[commonIndex]).append(separator);
		commonIndex++;
	}

	if (commonIndex == 0) {
		throw new PathResolutionException(java.util.ResourceBundle.getBundle("net.rptools.maptool.launcher.Messages")
				.getString("path.relative.noCommon"));
	}

	// ---- determine if base is file or directory (new precedence rules) ----
	boolean baseIsFile;
	if (basePath.endsWith(String.valueOf(separator))) {
		baseIsFile = false;
	} else {
		java.io.File baseFile = new java.io.File(normalizedBasePath);
		baseIsFile = baseFile.exists() ? baseFile.isFile() : true;
	}

	// ---- backtracking ----
	StringBuilder relativeBuilder = new StringBuilder();
	if (baseSegments.length != commonIndex) {
		int numDirsUp = baseIsFile ? (baseSegments.length - commonIndex - 1)
							: (baseSegments.length - commonIndex);
		for (int i = 0; i < numDirsUp; i++) {
			relativeBuilder.append("..").append(separator);
		}
	}

	// ---- append target remainder with separator correction ----
	int sliceStart = commonBuilder.length();
	if (sliceStart > 0 && sliceStart < normalizedTargetPath.length()
			&& normalizedTargetPath.charAt(sliceStart) == separator) {
		sliceStart++;
	}
	if (sliceStart < normalizedTargetPath.length()) {
		relativeBuilder.append(normalizedTargetPath.substring(sliceStart));
	}

	String result = relativeBuilder.toString();

	// ---- trim trailing separator for backtracking-only results ----
	if (result.endsWith(String.valueOf(separator)) && result.startsWith("..")) {
		result = result.substring(0, result.length() - 1);
	}

	return result;
}