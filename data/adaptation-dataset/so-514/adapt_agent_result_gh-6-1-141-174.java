public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();
        if (dialog == null) {
            return;
        }
        Toolbar bar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            View listView = dialog.findViewById(android.R.id.list);
            ViewParent parent = listView != null ? listView.getParent() : null;
            // Traverse parents defensively to find a suitable LinearLayout root
            while (parent != null && !(parent instanceof LinearLayout)) {
                parent = parent.getParent();
            }
            if (parent instanceof LinearLayout) {
                LinearLayout root = (LinearLayout) parent;
                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                root.addView(bar, 0); // insert at top
            }
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            if (root != null && root.getChildCount() > 0 && root.getChildAt(0) instanceof ListView) {
                ListView content = (ListView) root.getChildAt(0);
                root.removeAllViews();
                bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                int height;
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                } else {
                    height = bar.getHeight();
                }
                content.setPadding(0, height, 0, 0);
                root.addView(content);
                root.addView(bar);
            }
        }
        if (bar != null) {
            bar.setTitle(preferenceScreen.getTitle());
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogI) {
                    if (AppData.getLoginSuccessful()) {
                        dialogI.dismiss();
                    } else {
                        showNotConnectedDialog(dialog);
                    }
                }
            });
            bar.setNavigationOnClickListener(new View.OnClickListener() {
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
    }