private void setupActionBar() {
    // Explicit helper to set up the Toolbar; must be called by lifecycle or manually as needed
    final Toolbar bar;

    // Modern layout path: Ice Cream Sandwich (API 14) and above
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
        // PreferenceActivity uses a ListView; walk up the parent chain defensively
        View list = findViewById(android.R.id.list);
        if (list == null) {
            return;
        }
        View parent = list.getParent();
        if (!(parent instanceof View)) {
            return;
        }
        View parent2 = ((View) parent).getParent();
        if (!(parent2 instanceof View)) {
            return;
        }
        View parent3 = ((View) parent2).getParent();
        if (!(parent3 instanceof LinearLayout)) {
            return;
        }
        LinearLayout root = (LinearLayout) parent3;

        View inflated = android.view.LayoutInflater.from(this)
                .inflate(org.onebusaway.android.R.layout.settings_toolbar, root, false);
        if (!(inflated instanceof Toolbar)) {
            return;
        }
        bar = (Toolbar) inflated;

        // Insert toolbar at the top of the layout
        root.addView(bar, 0);
    } else {
        // Legacy layout path: Gingerbread and below require manual content removal and padding
        View contentRoot = findViewById(android.R.id.content);
        if (!(contentRoot instanceof ViewGroup)) {
            return;
        }
        ViewGroup root = (ViewGroup) contentRoot;
        if (root.getChildCount() == 0 || !(root.getChildAt(0) instanceof ListView)) {
            return;
        }
        ListView content = (ListView) root.getChildAt(0);

        // Remove existing views so we can re-add with a toolbar
        root.removeAllViews();

        View inflated = android.view.LayoutInflater.from(this)
                .inflate(org.onebusaway.android.R.layout.settings_toolbar, root, false);
        if (!(inflated instanceof Toolbar)) {
            return;
        }
        bar = (Toolbar) inflated;

        // Calculate action bar height to offset the ListView content
        int height = bar.getHeight();
        android.util.TypedValue tv = new android.util.TypedValue();
        if (getTheme() != null
                && getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            height = android.util.TypedValue.complexToDimensionPixelSize(
                    tv.data, getResources().getDisplayMetrics());
        }

        // Apply top padding so content is not obscured by the toolbar
        content.setPadding(0, height, 0, 0);

        root.addView(content);
        root.addView(bar);
    }

    // Back navigation finishes this activity, matching prior behavior
    bar.setNavigationOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    });
  }