private void setupActionBar() {
    Toolbar toolbar = null;
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            View list = findViewById(android.R.id.list);
            if (list != null) {
                ViewParent p1 = list.getParent();
                ViewParent p2 = p1 != null ? p1.getParent() : null;
                ViewParent p3 = p2 != null ? p2.getParent() : null;
                if (p3 instanceof ViewGroup) {
                    ViewGroup root = (ViewGroup) p3;
                    toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                    root.addView(toolbar, 0);
                }
            }
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            if (root != null && root.getChildCount() > 0 && root.getChildAt(0) instanceof ListView) {
                ListView content = (ListView) root.getChildAt(0);
                root.removeAllViews();
                toolbar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
                int height;
                TypedValue tv = new TypedValue();
                if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
                } else {
                    height = toolbar.getHeight();
                }
                content.setPadding(0, height, 0, 0);
                root.addView(content);
                root.addView(toolbar);
            }
        }

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    } catch (Exception ex) {
        Log.w(TAG, "Failed to setup ActionBar", ex);
    }
}