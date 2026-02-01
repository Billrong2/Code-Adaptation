private static void breakManually(TextView tv, Editable editable) {
    // Null-safe early exits
    if (tv == null || editable == null) {
        return;
    }

    int width = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
    // Can't break with zero or negative width
    if (width <= 0) {
        return;
    }

    Paint paint = tv.getPaint();
    final String text = editable.toString();
    final int originalLength = text.length();
    if (originalLength == 0) {
        return;
    }

    // Precompute widths for the original text only
    float[] widths = new float[originalLength];
    paint.getTextWidths(text, widths);

    float curWidth = 0.0f;
    int lastWSPos = -1; // last whitespace position in the editable
    int strPos = 0;     // current position in the editable
    final char newLine = '\n';
    final String newLineStr = "\n";
    boolean reset = false;
    int insertCount = 0; // number of inserted newlines so far

    while (strPos < editable.length()) {
        // Map editable position to original text index to avoid width drift
        int originalIndex = strPos - insertCount;
        if (originalIndex >= 0 && originalIndex < widths.length) {
            curWidth += widths[originalIndex];
        }

        char curChar = editable.charAt(strPos);

        if (curChar == newLine) {
            reset = true;
            lastWSPos = -1;
        } else if (Character.isWhitespace(curChar)) {
            lastWSPos = strPos;
        } else if (curWidth > width) {
            // Exceeded width: prefer breaking at whitespace, otherwise force mid-word break
            int breakPos = (lastWSPos >= 0) ? lastWSPos : strPos;
            editable.replace(breakPos, breakPos + 1, newLineStr);
            insertCount++;
            strPos = breakPos; // re-evaluate from the break position
            lastWSPos = -1;
            reset = true;
        }

        if (reset) {
            curWidth = 0.0f;
            reset = false;
        }

        strPos++;
    }

    // Only update the TextView if modifications were made
    if (insertCount > 0) {
        tv.setText(editable);
    }
}