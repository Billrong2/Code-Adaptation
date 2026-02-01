public String format(final LogRecord record) {
    final StringBuilder messageBuilder = new StringBuilder();
    final String lineSeparator = System.lineSeparator();

    final String levelName = record != null && record.getLevel() != null
            ? record.getLevel().getName()
            : "";
    final String levelPrefix = String.format("%7s: ", levelName);

    messageBuilder.append(levelPrefix)
                  .append(formatMessage(record))
                  .append(lineSeparator);

    final Throwable thrown = record != null ? record.getThrown() : null;
    if (thrown != null) {
        messageBuilder.append("Throwable occurred: "); //$NON-NLS-1$
        final StringWriter stringWriter = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(stringWriter)) {
            thrown.printStackTrace(printWriter);
        }
        messageBuilder.append(stringWriter.toString());
    }

    return messageBuilder.toString();
}