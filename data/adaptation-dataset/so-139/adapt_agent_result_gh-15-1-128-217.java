public static String getRelativePath(String targetPath, String basePath) throws PathResolutionException {
        // Null-safety
        if (targetPath == null || basePath == null) {
            return null;
        }

        final String separator = java.io.File.separator;

        // Normalize paths without trailing separators
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        if (normalizedTargetPath == null || normalizedBasePath == null) {
            return null;
        }

        // Identical-path handling (ignore trailing separators via normalization)
        if (FilenameUtils.equalsNormalizedOnSystem(normalizedTargetPath, normalizedBasePath)) {
            return "";
        }

        String[] baseParts = normalizedBasePath.split(java.util.regex.Pattern.quote(separator));
        String[] targetParts = normalizedTargetPath.split(java.util.regex.Pattern.quote(separator));

        // Find common path prefix
        StringBuilder commonBuilder = new StringBuilder();
        int commonIndex = 0;
        while (commonIndex < targetParts.length && commonIndex < baseParts.length
                && targetParts[commonIndex].equals(baseParts[commonIndex])) {
            commonBuilder.append(targetParts[commonIndex]).append(separator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // Likely different roots (e.g., different drive letters)
            throw new PathResolutionException(
                    "No common path element for '" + normalizedTargetPath + "' and '" + normalizedBasePath + "'");
        }

        // Heuristic to determine whether base is a file or directory
        boolean baseIsFile = true;
        java.io.File baseResource = new java.io.File(normalizedBasePath);
        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();
        } else if (basePath.endsWith(separator)) {
            baseIsFile = false;
        }

        StringBuilder relativeBuilder = new StringBuilder();

        if (baseParts.length != commonIndex) {
            int numDirsUp = baseIsFile
                    ? baseParts.length - commonIndex - 1
                    : baseParts.length - commonIndex;

            for (int i = 0; i < numDirsUp; i++) {
                relativeBuilder.append("..").append(separator);
            }
        }

        int commonLength = commonBuilder.length();
        if (commonLength < normalizedTargetPath.length()) {
            relativeBuilder.append(normalizedTargetPath.substring(commonLength));
        }

        return relativeBuilder.toString();
    }