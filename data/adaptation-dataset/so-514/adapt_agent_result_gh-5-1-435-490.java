private void setUpNestedScreen(final PreferenceScreen preferenceScreen) {
    if (preferenceScreen == null) {
        return;
    }

    final Dialog dialog = preferenceScreen.getDialog();
    if (dialog == null) {
        return;
    }

    // Ensure dialog-aware cancel handling instead of finishing the activity
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {
            if (AppData.getLoginSuccessful()) {
                dialogInterface.dismiss();
            } else {
                showNotConnectedDialog(dialog);
            }
        }
    });

    Toolbar toolbar = null;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        // ICS+ uses a simpler parent traversal inside the dialog
        final View listView = dialog.findViewById(android.R.id.list);
        if (listView != null) {
            View parent = listView.getParent();
            if (parent instanceof ViewGroup) {
                final ViewGroup root = (ViewGroup) parent;
                toolbar = (Toolbar) LayoutInflater.from(this)
                        .inflate(R.layout.settings_toolbar, root, false);
                root.addView(toolbar, 0); // insert at top
            }
        }
    } else {
        // Pre-ICS: re-parent content and apply top padding inside dialog hierarchy
        final View contentRoot = dialog.findViewById(android.R.id.content);
        if (contentRoot instanceof ViewGroup) {
            final ViewGroup root = (ViewGroup) contentRoot;
            if (root.getChildCount() > 0 && root.getChildAt(0) instanceof ListView) {
                final ListView content = (ListView) root.getChildAt(0);
                root.removeAllViews();

                toolbar = (Toolbar) LayoutInflater.from(this)
                        .inflate(R.layout.settings_toolbar, root, false);

                int height;
                final TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                } else {
                    height = toolbar.getLayoutParams() != null ? toolbar.getLayoutParams().height : 0;
                }

                content.setPadding(0, height, 0, 0);
                root.addView(content);
                root.addView(toolbar, 0);
            }
        }
    }

    if (toolbar == null) {
        return;
    }

    // Set toolbar title from the PreferenceScreen
    if (preferenceScreen.getTitle() != null) {
        toolbar.setTitle(preferenceScreen.getTitle());
    }

    // Navigation behaves like dialog-aware back
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (AppData.getLoginSuccessful()) {
                dialog.dismiss();
            } else {
                showNotConnectedDialog(dialog);
            }
        }
    });
}