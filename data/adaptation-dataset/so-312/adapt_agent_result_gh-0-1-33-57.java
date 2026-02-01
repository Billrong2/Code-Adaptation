public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<T> future = executor.submit(callable);
    executor.shutdown(); // does not cancel the already-scheduled task
    try {
      return future.get(timeout, timeUnit);
    } catch (TimeoutException e) {
      // remove this if you do not want to cancel the job in progress
      // or set the argument to 'false' if you do not want to interrupt the thread
      future.cancel(true);
      throw e;
    } catch (ExecutionException e) {
      Throwable cause = e.getCause();
      if (cause instanceof Error) {
        throw (Error) cause;
      }
      // Do not unwrap and rethrow Exception causes; rethrow the original ExecutionException
      throw e;
    }
  }