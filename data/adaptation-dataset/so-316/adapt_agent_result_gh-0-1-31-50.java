/**
     * Frees view-related resources to help reduce memory leaks by clearing backgrounds,
     * image bitmaps, and recursively unbinding child views.
     * Can be safely called with a null view.
     */
    public static void freeViewResources(android.view.View view) {
        if (view == null) {
            return;
        }

        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }

        if (view instanceof android.widget.ImageView) {
            android.widget.ImageView imageView = (android.widget.ImageView) view;
            imageView.setImageBitmap(null);
        } else if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup viewGroup = (android.view.ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                freeViewResources(viewGroup.getChildAt(i));
            }

            if (!(view instanceof android.widget.AdapterView)) {
                viewGroup.removeAllViews();
            }
        }
    }