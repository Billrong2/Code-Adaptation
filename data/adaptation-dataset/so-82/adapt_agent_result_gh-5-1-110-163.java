private void setUpNestedScreen(final android.preference.PreferenceScreen preferenceScreen) {
    android.app.Dialog dialog = null;
    android.support.v7.widget.Toolbar toolbar = null;
    try {
        if (preferenceScreen == null) {
            Log.w(TAG, "PreferenceScreen is null, skipping toolbar setup.");
            return;
        }
        dialog = preferenceScreen.getDialog();
        if (dialog == null) {
            Log.w(TAG, "Dialog is null for PreferenceScreen: " + preferenceScreen.getTitle());
            return;
        }
        if (dialog.getWindow() == null) {
            Log.w(TAG, "Dialog window is null for PreferenceScreen: " + preferenceScreen.getTitle());
            return;
        }

        android.view.View rootView = dialog.getWindow().getDecorView();
        if (rootView == null) {
            Log.w(TAG, "Root view is null for PreferenceScreen dialog: " + preferenceScreen.getTitle());
            return;
        }

        android.widget.ListView listView = (android.widget.ListView) rootView.findViewById(android.R.id.list);
        if (listView == null) {
            Log.w(TAG, "ListView not found in PreferenceScreen dialog: " + preferenceScreen.getTitle());
            return;
        }

        android.view.ViewParent parent = listView.getParent();
        if (!(parent instanceof android.widget.LinearLayout)) {
            Log.w(TAG, "Unsupported parent layout for PreferenceScreen dialog (expected LinearLayout): " + preferenceScreen.getTitle());
            return;
        }

        android.widget.LinearLayout linearLayout = (android.widget.LinearLayout) parent;

        // Guard against duplicate toolbar injection
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            if (linearLayout.getChildAt(i) instanceof android.support.v7.widget.Toolbar) {
                Log.d(TAG, "Toolbar already exists for PreferenceScreen: " + preferenceScreen.getTitle());
                return;
            }
        }

        android.content.Context context = dialog.getContext();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            toolbar = (android.support.v7.widget.Toolbar) android.view.LayoutInflater.from(context)
                    .inflate(com.erakk.lnreader.R.layout.settings_toolbar, linearLayout, false);
            linearLayout.addView(toolbar, 0);
        } else {
            // Pre-ICS fallback: preserve padding/height logic but apply to dialog content
            toolbar = (android.support.v7.widget.Toolbar) android.view.LayoutInflater.from(context)
                    .inflate(com.erakk.lnreader.R.layout.settings_toolbar, linearLayout, false);

            int height;
            android.util.TypedValue tv = new android.util.TypedValue();
            if (context.getTheme() != null && context.getTheme().resolveAttribute(com.erakk.lnreader.R.attr.actionBarSize, tv, true)) {
                height = android.util.TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            } else {
                height = toolbar.getMeasuredHeight() > 0 ? toolbar.getMeasuredHeight() : 0;
            }

            listView.setPadding(listView.getPaddingLeft(), height, listView.getPaddingRight(), listView.getPaddingBottom());
            linearLayout.addView(toolbar, 0);
        }
    } catch (ClassCastException cce) {
        Log.e(TAG, "ClassCastException while setting up nested PreferenceScreen toolbar: " + (preferenceScreen != null ? preferenceScreen.getTitle() : "unknown"), cce);
    } catch (Exception ex) {
        Log.e(TAG, "Failed to set up toolbar for nested PreferenceScreen: " + (preferenceScreen != null ? preferenceScreen.getTitle() : "unknown"), ex);
    }

    // Attach title and navigation only if toolbar was successfully created
    if (toolbar != null) {
        try {
            if (preferenceScreen.getTitle() != null) {
                toolbar.setTitle(preferenceScreen.getTitle());
            }
            final android.app.Dialog finalDialog = dialog;
            toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View v) {
                    try {
                        if (finalDialog != null && finalDialog.isShowing()) {
                            finalDialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to dismiss PreferenceScreen dialog: " + preferenceScreen.getTitle(), e);
                    }
                }
            });
        } catch (Exception ex) {
            Log.w(TAG, "Failed to finalize toolbar setup for PreferenceScreen: " + preferenceScreen.getTitle(), ex);
        }
    }
}