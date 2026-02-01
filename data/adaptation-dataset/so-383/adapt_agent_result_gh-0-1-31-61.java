@Override
protected void showDialog(Bundle state) {
    final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);

    builder.title(getDialogTitle());
    builder.icon(getDialogIcon());
    builder.negativeText(getNegativeButtonText());

    final CharSequence[] entries = getEntries();
    final CharSequence[] entryValues = getEntryValues();
    final int preselect = getValue() != null ? findIndexOfValue(getValue()) : -1;

    if (entries != null) {
        builder.items(entries);
        builder.itemsCallbackSingleChoice(preselect, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                if (which >= 0 && entryValues != null && which < entryValues.length) {
                    String value = entryValues[which].toString();
                    if (callChangeListener(value) && isPersistent()) {
                        setValue(value);
                    }
                }
                return true;
            }
        });
    }

    final View contentView = onCreateDialogView();
    if (contentView != null) {
        onBindDialogView(contentView);
        builder.customView(contentView, false);
    } else {
        builder.content(getDialogMessage());
    }

    builder.show();
}
