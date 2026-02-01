public static String relativePath(@NotNull final String targetPath, @NotNull final String basePath, @NotNull final String pathSeparator) {
        if (targetPath == null || basePath == null) {
            throw new IllegalArgumentException("targetPath and basePath must not be null");
        }

        // Normalize the paths
        String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
        String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);

        // Undo the changes to the separators made by normalization
        if ("/".equals(pathSeparator)) {
            normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);
        } else if ("\\".equals(pathSeparator)) {
            normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
            normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);
        } else {
            throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
        }

        String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
        String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));

        // First get all the common elements
        StringBuilder common = new StringBuilder();
        int commonIndex = 0;
        while (commonIndex < target.length && commonIndex < base.length
                && target[commonIndex].equals(base[commonIndex])) {
            common.append(target[commonIndex]).append(pathSeparator);
            commonIndex++;
        }

        if (commonIndex == 0) {
            // No common path element (e.g. different drive letters)
            throw new PathResolutionException("No common path element found for '" + normalizedTargetPath
                    + "' and '" + normalizedBasePath + "'");
        }

        // If the target and base resolve to the same path, return current directory
        if (commonIndex == target.length) {
            return ".";
        }

        // Determine whether the base refers to a file or a directory
        boolean baseIsFile = true;
        File baseResource = new File(normalizedBasePath);
        if (baseResource.exists()) {
            baseIsFile = baseResource.isFile();
        } else if (basePath.endsWith(pathSeparator)) {
            baseIsFile = false;
        }

        StringBuilder relative = new StringBuilder();
        if (base.length != commonIndex) {
            int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;
            for (int i = 0; i < numDirsUp; i++) {
                relative.append("..").append(pathSeparator);
            }
        }

        // If the common prefix already covers the whole target, represent current directory
        if (common.length() >= normalizedTargetPath.length()) {
            return ".";
        }

        relative.append(normalizedTargetPath.substring(common.length()));
        return relative.toString();
    }