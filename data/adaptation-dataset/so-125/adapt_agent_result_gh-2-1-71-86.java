private void applyLetterSpacing() {
    final int length = originalText.length();
    StringBuilder builder = new StringBuilder(length * 2);
    for (int i = 0; i < length; i++) {
        builder.append(originalText.charAt(i));
        if (i + 1 < length) {
            builder.append('\u00A0');
        }
    }
    final String finalString = builder.toString();
    SpannableString finalText = new SpannableString(finalString);
    if (finalString.length() > 1) {
        final float scale = (letterSpacing + 1.0F) / 10.0F;
        for (int i = 1; i < finalString.length(); i += 2) {
            finalText.setSpan(new ScaleXSpan(scale), i, i + 1, SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    super.setText(finalText, BufferType.SPANNABLE);
}