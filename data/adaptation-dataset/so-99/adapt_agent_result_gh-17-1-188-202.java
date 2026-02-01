@Override public Response intercept(okhttp3.Interceptor.Chain chain) throws java.io.IOException {
    okhttp3.Request request = chain.request();

    long t1 = System.nanoTime();
    android.util.Log.d(TAG, String.format(java.util.Locale.US,
            "Sending request %s on %s%n%s",
            request.url(), chain.connection(), request.headers()));

    okhttp3.Response response = chain.proceed(request);

    long t2 = System.nanoTime();
    android.util.Log.d(TAG, String.format(java.util.Locale.US,
            "Received response for %s in %.1fms, status %d%n%s",
            response.request().url(), (t2 - t1) / 1e6d, response.code(), response.headers()));

    return response;
}