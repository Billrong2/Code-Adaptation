public View getInfoContents(Marker marker) {
    if (marker == null) return null;

    final String titleText = marker.getTitle();
    final String snippetText = marker.getSnippet();

    if (titleText == null || titleText.length() == 0) return null;

    final android.widget.LinearLayout info = new android.widget.LinearLayout(mContext);
    info.setOrientation(android.widget.LinearLayout.VERTICAL);

    final android.widget.TextView title = new android.widget.TextView(mContext);
    title.setTextColor(android.graphics.Color.BLACK);
    title.setGravity(android.view.Gravity.CENTER);
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    title.setText(titleText);
    info.addView(title);

    if (snippetText != null && snippetText.length() > 0) {
        final android.widget.TextView snippet = new android.widget.TextView(mContext);
        snippet.setTextColor(android.graphics.Color.GRAY);
        snippet.setGravity(android.view.Gravity.CENTER);
        snippet.setText(snippetText);
        info.addView(snippet);
    }

    return info;
}