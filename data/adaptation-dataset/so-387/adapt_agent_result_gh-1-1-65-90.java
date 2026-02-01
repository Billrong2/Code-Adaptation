/**
		 * Extracts and logs exceptions thrown by tasks executed in this {@link java.util.concurrent.ThreadPoolExecutor}.
		 * <p>
		 * If the {@link Runnable} is a {@link java.util.concurrent.Future}, this method attempts to obtain its result
		 * in order to surface any hidden exceptions. Any detected {@link Throwable} is logged at warn level together
		 * with contextual information about this executor instance.
		 */
		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			super.afterExecute(r, t);
			if (t == null && r instanceof Future<?>) {
				try {
					Future<?> future = (Future<?>) r;
					if (future.isDone()) {
						future.get();
					}
				} catch (CancellationException ce) {
					t = ce;
				} catch (ExecutionException ee) {
					t = ee.getCause();
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt(); // ignore/reset
				}
			}
			if (t != null) {
				logger.warn("Uncaught exception in executor {}", this, t);
			}
		}