private void setupEvenlyDistributedToolbar(){
    // Use Display metrics to get Screen Dimensions
    android.view.Display display = getWindowManager().getDefaultDisplay();
    final android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
    display.getMetrics(metrics);

    // Bottom toolbar (menu is assumed to be attached elsewhere)
    final android.support.v7.widget.Toolbar toolbarBottom = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarBottom);
    if (toolbarBottom == null) {
        return;
    }

    // Add spacing on either side of the toolbar
    toolbarBottom.setContentInsetsAbsolute(10, 10);

    final int childCount = toolbarBottom.getChildCount();
    if (childCount <= 0) {
        return;
    }

    // Get the Screen Width in pixels
    final int screenWidth = metrics.widthPixels;

    // Create the Toolbar Params based on the screenWidth
    final android.support.v7.widget.Toolbar.LayoutParams toolbarParams =
            new android.support.v7.widget.Toolbar.LayoutParams(screenWidth, android.support.v7.widget.Toolbar.LayoutParams.WRAP_CONTENT);

    // Loop through the child Items
    for (int i = 0; i < childCount; i++){
        final android.view.View toolbarChild = toolbarBottom.getChildAt(i);
        if (toolbarChild instanceof android.view.ViewGroup){
            // Set its layout params
            toolbarChild.setLayoutParams(toolbarParams);

            final android.view.ViewGroup toolbarChildGroup = (android.view.ViewGroup) toolbarChild;
            final int innerChildCount = toolbarChildGroup.getChildCount();
            if (innerChildCount <= 0) {
                continue;
            }

            // Compute the item widths based on this count & screen size
            final int itemWidth  = (screenWidth / innerChildCount);

            // Create layout params for the ActionMenuView items
            final android.support.v7.widget.ActionMenuView.LayoutParams params =
                    new android.support.v7.widget.ActionMenuView.LayoutParams(itemWidth, android.support.v7.widget.Toolbar.LayoutParams.WRAP_CONTENT);

            // Loop through the menu item views
            for (int j = 0; j < innerChildCount; j++){
                final android.view.View menuItemView = toolbarChildGroup.getChildAt(j);
                if (menuItemView instanceof android.support.v7.view.menu.ActionMenuItemView){
                    menuItemView.setLayoutParams(params);
                }
            }
        }
    }
}