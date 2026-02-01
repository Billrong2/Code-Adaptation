public android.view.View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
    if (marker == null) return null;

    android.content.Context context = PokeFinderActivity.this;

    android.widget.LinearLayout info = new android.widget.LinearLayout(context);
    info.setOrientation(android.widget.LinearLayout.VERTICAL);

    android.widget.TextView title = new android.widget.TextView(context);
    title.setTextColor(android.graphics.Color.BLACK);
    title.setGravity(android.view.Gravity.CENTER);
    title.setTypeface(null, android.graphics.Typeface.BOLD);
    title.setText(marker.getTitle());

    info.addView(title);

    String snippetText = marker.getSnippet();
    if (snippetText != null && !snippetText.trim().isEmpty()) {
        android.widget.TextView snippet = new android.widget.TextView(context);
        snippet.setTextColor(android.graphics.Color.GRAY);
        snippet.setGravity(android.view.Gravity.CENTER);
        snippet.setText(snippetText);
        info.addView(snippet);
    }

    return info;
}