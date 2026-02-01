public static File getRelativeFile(final File target, final File base) {
    if (target == null) {
        throw new IllegalArgumentException("target must not be null");
    }
    if (base == null) {
        throw new IllegalArgumentException("base must not be null");
    }

    final String basePath;
    final String targetPath;
    try {
        basePath = base.getCanonicalPath();
        targetPath = target.getCanonicalPath();
    } catch (java.io.IOException e) {
        throw new com.jopdesign.common.misc.AppInfoError(
                "Failed to resolve canonical paths for target='" + target + "' and base='" + base + "'",
                e);
    }

    // Adapted from StackOverflow (relative path computation)
    String[] baseComponents = basePath.split(java.util.regex.Pattern.quote(java.io.File.separator));
    String[] targetComponents = targetPath.split(java.util.regex.Pattern.quote(java.io.File.separator));

    // skip common components
    int index = 0;
    for (; index < targetComponents.length && index < baseComponents.length; ++index) {
        if (!targetComponents[index].equals(baseComponents[index])) {
            break;
        }
    }

    StringBuilder result = new StringBuilder();
    if (index != baseComponents.length) {
        // backtrack to base directory
        for (int i = index; i < baseComponents.length; ++i) {
            result.append("..").append(java.io.File.separator);
        }
    }
    for (; index < targetComponents.length; ++index) {
        result.append(targetComponents[index]).append(java.io.File.separator);
    }
    if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\")) {
        // remove final path separator
        if (result.length() >= java.io.File.separator.length()) {
            result.delete(result.length() - java.io.File.separator.length(), result.length());
        }
    }
    return new java.io.File(result.toString());
}