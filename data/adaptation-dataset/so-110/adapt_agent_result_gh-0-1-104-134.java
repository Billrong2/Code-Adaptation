@Override
public boolean askForOk(String data) {
    // Ensure we are on the UI thread; this method relies on a blocking Looper
    if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
        throw new IllegalStateException("askForOk must be called on the UI thread");
    }

    // reset result before showing dialog
    askForOkResult = false;

    // Dedicated handler that breaks out of Looper.loop()
    final Handler handler = new HandlerClass();

    // Build and show the dialog using this Activity as Context
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle("Permission request");
    alert.setMessage(data);
    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            askForOkResult = true;
            handler.sendMessage(handler.obtainMessage());
        }
    });
    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            askForOkResult = false;
            handler.sendMessage(handler.obtainMessage());
        }
    });
    alert.show();

    // Block until the handler throws the RuntimeException to exit the loop
    try {
        Looper.loop();
    } catch (RuntimeException e) {
        // Intentionally used to break out of Looper.loop()
    }

    return askForOkResult;
}