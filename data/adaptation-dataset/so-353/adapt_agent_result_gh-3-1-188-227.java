    /**
     * Returns the path of one File relative to another.
     * <p>
     * This method attempts to resolve canonical paths to build a relative path
     * using ".." segments and platform-specific separators. If canonical path
     * resolution fails, the target's absolute path is returned as a fallback.
     *
     * @param target the target file or directory
     * @param base   the base directory
     * @return target's path relative to the base directory, or an absolute path on failure
     */
    public static String getRelativePath(File target, File base) {
        if (target == null || base == null) {
            return null;
        }

        final String targetCanonicalPath;
        final String baseCanonicalPath;
        try {
            targetCanonicalPath = target.getCanonicalPath();
            baseCanonicalPath = base.getCanonicalPath();
        } catch (java.io.IOException e) {
            // Fallback to absolute path if canonical resolution fails
            return target.getAbsolutePath();
        }

        String[] baseComponents = baseCanonicalPath.split(java.util.regex.Pattern.quote(File.separator));
        String[] targetComponents = targetCanonicalPath.split(java.util.regex.Pattern.quote(File.separator));

        // skip common components
        int index = 0;
        for (; index < targetComponents.length && index < baseComponents.length; ++index) {
            if (!targetComponents[index].equals(baseComponents[index])) {
                break;
            }
        }

        StringBuilder relativePath = new StringBuilder();

        // backtrack to base directory
        if (index < baseComponents.length) {
            for (int i = index; i < baseComponents.length; ++i) {
                relativePath.append("..").append(File.separator);
            }
        }

        // append remaining target components
        for (; index < targetComponents.length; ++index) {
            if (targetComponents[index].length() > 0) {
                relativePath.append(targetComponents[index]).append(File.separator);
            }
        }

        // remove final path separator if target is not a directory path
        String targetPath = target.getPath();
        if (relativePath.length() > 0
                && !targetPath.endsWith("/")
                && !targetPath.endsWith("\\")) {
            // delete using hardcoded "/" length (1)
            relativePath.delete(relativePath.length() - 1, relativePath.length());
        }

        return relativePath.toString();
    }