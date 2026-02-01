private void applyLetterSpacing() {
        // Always rebuild text with non-breaking spaces between characters
        final int length = originalText.length();
        StringBuilder builder = new StringBuilder(Math.max(0, length * 2 - 1));
        for (int i = 0; i < length; i++) {
            builder.append(originalText.charAt(i));
            if (i + 1 < length) {
                builder.append("\u00A0");
            }
        }

        SpannableString finalText = new SpannableString(builder.toString());

        // Optionally skip span application when text is too short
        if (finalText.length() > 1) {
            final float scale = (letterSpacing + 1.0F) / 10.0F;
            for (int i = 1; i < finalText.length(); i += 2) {
                finalText.setSpan(new ScaleXSpan(scale), i, i + 1, SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        super.setText(finalText, BufferType.SPANNABLE);
    }