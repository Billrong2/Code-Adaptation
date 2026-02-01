@Override
public void flush() {
    // If there is nothing buffered, do nothing
    if (count == 0) {
        return;
    }

    // Convert buffered bytes to String using platform default charset
    final String message = new String(buf, 0, count);

    try {
        // Suppress logging of blank lines (only line separator)
        if (!LINE_SEPERATOR.equals(message)) {
            if (isError) {
                log.error(message);
            } else {
                log.info(message);
            }
        }
    } finally {
        // Always reset buffer after flush attempt
        reset();
    }
}