@Override
public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
    if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
        // Only intercept back press at the task root
        if (!isTaskRoot()) {
            return super.onKeyDown(keyCode, event);
        }

        // Respect user preference for confirm quit/logout
        if (!mSettings.isConfirmQuitOrLogout()) {
            return super.onKeyDown(keyCode, event);
        }

        // Avoid showing dialogs when activity is no longer valid
        if (isFinishing() || (android.os.Build.VERSION.SDK_INT >= 17 && isDestroyed())) {
            return super.onKeyDown(keyCode, event);
        }

        // Show themed confirmation dialog
        new android.app.AlertDialog.Builder(
                new android.view.ContextThemeWrapper(this, mSettings.getDialogTheme()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(com.andrewshu.android.reddit.R.string.quit)
                .setMessage(com.andrewshu.android.reddit.R.string.really_quit)
                .setPositiveButton(com.andrewshu.android.reddit.R.string.yes,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(android.content.DialogInterface dialog, int which) {
                                // Finish this activity
                                finish();
                            }
                        })
                .setNegativeButton(com.andrewshu.android.reddit.R.string.no, null)
                .show();

        return true;
    }

    return super.onKeyDown(keyCode, event);
}