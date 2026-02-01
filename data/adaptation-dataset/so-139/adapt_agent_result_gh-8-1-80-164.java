public static String getRelativePath(String basePath, String targetPath, String pathSeparator) {
    // Normalize inputs using project utility; handle nulls per contract
    String normalizedTarget = normalizeNoEndSeparator(targetPath, pathSeparator);
    if (normalizedTarget == null) {
      return "";
    }

    String normalizedBase = normalizeNoEndSeparator(basePath, pathSeparator);
    if (normalizedBase == null) {
      return targetPath;
    }

    // Split paths into segments
    String[] baseParts = normalizedBase.split(Pattern.quote(pathSeparator));
    String[] targetParts = normalizedTarget.split(Pattern.quote(pathSeparator));

    // Find common prefix
    StringBuilder common = new StringBuilder();
    int commonIndex = 0;
    while (commonIndex < targetParts.length && commonIndex < baseParts.length
        && targetParts[commonIndex].equals(baseParts[commonIndex])) {
      common.append(targetParts[commonIndex]).append(pathSeparator);
      commonIndex++;
    }

    // If there is no common prefix, return normalized target as a safe fallback
    if (commonIndex == 0) {
      return normalizedTarget;
    }

    // Heuristic to determine whether base refers to a file or directory
    boolean baseIsFile = true;
    try {
      java.io.File baseResource = new java.io.File(normalizedBase);
      if (baseResource.exists()) {
        baseIsFile = baseResource.isFile();
      } else if (basePath != null && basePath.endsWith(pathSeparator)) {
        baseIsFile = false;
      }
    } catch (Exception ignore) {
      // Fallback to default heuristic without throwing
    }

    StringBuilder relative = new StringBuilder();

    if (baseParts.length != commonIndex) {
      int numDirsUp = baseIsFile ? baseParts.length - commonIndex - 1 : baseParts.length - commonIndex;
      for (int i = 0; i < numDirsUp; i++) {
        relative.append("..").append(pathSeparator);
      }
    }

    // Append the remaining target path safely
    relative.append(safeSubstring(normalizedTarget, common.length()));
    return relative.toString();
  }