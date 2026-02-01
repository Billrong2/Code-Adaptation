private void repairToolbar() {
        // Inflate and insert a Toolbar into the PreferenceActivity layout, defensively
        android.support.v7.widget.Toolbar toolbar = null;
        android.view.View listView = findViewById(android.R.id.list);

        if (listView == null) {
            return; // unexpected layout, nothing to repair
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Newer APIs: walk up the parent hierarchy carefully
            android.view.ViewParent p1 = listView.getParent();
            android.view.ViewParent p2 = p1 != null ? p1.getParent() : null;
            android.view.ViewParent p3 = p2 != null ? p2.getParent() : null;

            if (p3 instanceof android.widget.LinearLayout) {
                android.widget.LinearLayout root = (android.widget.LinearLayout) p3;
                toolbar = (android.support.v7.widget.Toolbar) android.view.LayoutInflater
                        .from(this)
                        .inflate(R.layout.toolbar, root, false);
                if (toolbar != null) {
                    toolbar.setId(R.id.toolbar);
                    root.addView(toolbar, 0); // insert at top
                }
            }
        } else {
            // Older APIs: recompose content view
            android.view.View rootView = findViewById(android.R.id.content);
            if (rootView instanceof android.view.ViewGroup) {
                android.view.ViewGroup root = (android.view.ViewGroup) rootView;
                android.view.View firstChild = root.getChildCount() > 0 ? root.getChildAt(0) : null;

                if (firstChild instanceof android.widget.ListView) {
                    android.widget.ListView content = (android.widget.ListView) firstChild;
                    root.removeAllViews();

                    toolbar = (android.support.v7.widget.Toolbar) android.view.LayoutInflater
                            .from(this)
                            .inflate(R.layout.toolbar, root, false);
                    if (toolbar != null) {
                        toolbar.setId(R.id.toolbar);

                        int height = 0;
                        android.util.TypedValue tv = new android.util.TypedValue();
                        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                            height = android.util.TypedValue.complexToDimensionPixelSize(
                                    tv.data, getResources().getDisplayMetrics());
                        } else if (toolbar.getLayoutParams() != null) {
                            height = toolbar.getLayoutParams().height;
                        }

                        if (height > 0) {
                            content.setPadding(0, height, 0, 0);
                        }

                        root.addView(content);
                        root.addView(toolbar);
                    }
                }
            }
        }

        if (toolbar == null) {
            return;
        }

        // Register as SupportActionBar and enable navigation
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                finish();
            }
        });

        // Placeholder for future UI theming integration (Colorizer, ListView styling)
    }