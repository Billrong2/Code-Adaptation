public static String getRelativePath(File target, File base) {
        if (target == null) return null;
        if (base == null) return getFullPath(target);

        String targetPath = getFullPath(target);
        String basePath = getFullPath(base);

        if (targetPath == null || basePath == null) return targetPath;

        String[] baseComponents = basePath.split(java.util.regex.Pattern.quote(SEPARATOR));
        String[] targetComponents = targetPath.split(java.util.regex.Pattern.quote(SEPARATOR));

        int index = 0;
        int max = Math.min(baseComponents.length, targetComponents.length);
        for (; index < max; index++) {
            if (!baseComponents[index].equals(targetComponents[index])) break;
        }

        StringBuilder result = new StringBuilder();

        if (index < baseComponents.length) {
            for (int i = index; i < baseComponents.length; i++) {
                if (baseComponents[i].length() > 0) {
                    result.append("..").append(SEPARATOR);
                }
            }
        }

        for (int i = index; i < targetComponents.length; i++) {
            if (targetComponents[i].length() > 0) {
                result.append(targetComponents[i]).append(SEPARATOR);
            }
        }

        boolean targetEndsWithSeparator = target.getPath().endsWith("/") || target.getPath().endsWith("\\");
        if (!targetEndsWithSeparator && result.length() >= SEPARATOR.length()) {
            result.delete(result.length() - SEPARATOR.length(), result.length());
        }

        return result.toString();
    }