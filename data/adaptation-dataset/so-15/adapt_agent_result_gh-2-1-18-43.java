@Override
public String format(final LogRecord r) {
    final StringBuilder sb = new StringBuilder();
    final String levelName = (r != null && r.getLevel() != null) ? r.getLevel().getName() : "";
    sb.append(String.format("%7s: ", levelName));
    sb.append(formatMessage(r));
    sb.append(System.lineSeparator());

    final Throwable t = (r != null) ? r.getThrown() : null;
    if (t != null) {
        sb.append("Throwable occurred: ");
        final StringWriter sw = new StringWriter();
        try (final PrintWriter pw = new PrintWriter(sw)) {
            t.printStackTrace(pw);
        } catch (Exception e) {
            // print any exception that occurs during close/printing
            e.printStackTrace();
        }
        sb.append(sw.toString());
    }
    return sb.toString();
}