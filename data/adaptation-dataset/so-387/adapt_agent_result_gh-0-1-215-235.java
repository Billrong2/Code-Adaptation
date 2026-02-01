protected void afterExecute(Runnable r, Throwable t) {
    super.afterExecute(r, t);
    if (t == null && r instanceof java.util.concurrent.Future<?>) {
        try {
            java.util.concurrent.Future<?> future = (java.util.concurrent.Future<?>) r;
            if (future.isDone()) {
                future.get();
            }
        } catch (java.util.concurrent.CancellationException ce) {
            t = ce;
        } catch (java.util.concurrent.ExecutionException ee) {
            t = ee.getCause();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt(); // ignore/reset
        }
    }
    if (t != null) {
        String message = (t.getMessage() != null) ? t.getMessage() : "Unhandled exception in executor task";
        ParallelForEach.LOG.error(message, t);
        try {
            String flattened = org.apache.commons.lang3.exception.ExceptionUtils
                    .getStackTrace(t)
                    .replaceAll("\n", " ")
                    .replaceAll("\\s+", " ")
                    .trim();
            ParallelForEach.LOG.error("stacktrace: " + flattened);
        } catch (Exception loggingException) {
            // avoid secondary failures during logging
            ParallelForEach.LOG.error("Failed to stringify stacktrace for executor task", loggingException);
        }
    }
}