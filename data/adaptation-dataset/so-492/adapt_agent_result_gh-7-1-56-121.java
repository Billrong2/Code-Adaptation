public synchronized String format(java.util.logging.LogRecord record) {
    StringBuilder sb = new StringBuilder();

    // Minimize memory allocations here.
    dat.setTime(record.getMillis());
    args[0] = dat;

    // Date and time
    StringBuffer text = new StringBuffer();
    if (formatter == null) {
        formatter = new MessageFormat(format);
    }
    formatter.format(args, text, null);
    sb.append(text);
    sb.append(" ");

    // Logger name only (no source class fallback)
    String loggerName = record.getLoggerName();
    if (loggerName == null) {
        loggerName = "unknown";
    }
    sb.append(loggerName);

    // Optional method name
    if (record.getSourceMethodName() != null) {
        sb.append(" ");
        sb.append(record.getSourceMethodName());
    }

    // Thread id marker
    long threadId = Thread.currentThread().getId();
    sb.append(" - t: ");
    sb.append(threadId);
    sb.append(" ");

    String message = formatMessage(record);

    // Level
    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");

    // Indent - the more serious, the more indented.
    int iOffset = (1000 - record.getLevel().intValue()) / 100;
    for (int i = 0; i < iOffset; i++) {
        sb.append(" ");
    }

    sb.append(message);
    sb.append(lineSeparator);
    if (record.getThrown() != null) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            record.getThrown().printStackTrace(pw);
            pw.close();
            sb.append(sw.toString());
        } catch (Exception ex) {
            // ignore
        }
    }
    return sb.toString();
}