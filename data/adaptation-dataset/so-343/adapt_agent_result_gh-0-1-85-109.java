public static String[] stringSplitter(final String s, final int interval) {
    if ((s == null) || s.isEmpty()) {
        return new String[0];
    }

    if (interval == 0) {
        return new String[]{s};
    }

    int arrayLength = (int) Math.ceil((s.length() / (double) interval));
    String[] result = new String[arrayLength];

    int j = 0;
    int lastIndex = result.length - 1;
    for (int i = 0; i < lastIndex; i++) {
        result[i] = s.substring(j, j + interval);
        j += interval;
    }
    // Add the last bit (remainder)
    result[lastIndex] = s.substring(j);

    return result;
}