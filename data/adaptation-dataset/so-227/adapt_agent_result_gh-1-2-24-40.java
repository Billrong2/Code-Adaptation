@Override
public void onChanged() {
  final LinearListView parent = this.context;
  if (parent == null || parent.adapter == null) {
    super.onChanged();
    return;
  }

  final int existingChildCount = parent.getChildCount();
  final List<View> oldViews = new ArrayList<View>(existingChildCount);

  for (int i = 0; i < existingChildCount; i++) {
    final View child = parent.getChildAt(i);
    if (child != null) {
      oldViews.add(child);
    }
  }

  final Iterator<View> iter = oldViews.iterator();

  parent.removeAllViews();

  final int adapterCount = parent.adapter.getCount();
  for (int i = 0; i < adapterCount; i++) {
    final View convertView = iter.hasNext() ? iter.next() : null;
    try {
      parent.addView(parent.adapter.getView(i, convertView, parent));
    } catch (RuntimeException e) {
      android.util.Log.w("LinearListView", "Failed to create/add view at position " + i, e);
    }
  }

  super.onChanged();
}