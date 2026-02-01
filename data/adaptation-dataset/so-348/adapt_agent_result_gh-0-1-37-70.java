private String extractText(final Reader reader) {
    if (reader == null) {
      return "";
    }
    final StringBuilder builder = new StringBuilder();
    final HTMLEditorKit.ParserCallback parserCallback = new HTMLEditorKit.ParserCallback() {
      @Override
      public void handleText(final char[] data, final int pos) {
        if (data != null && data.length > 0) {
          builder.append(data);
        }
      }

      @Override
      public void handleStartTag(final HTML.Tag tag, final MutableAttributeSet attribute, final int pos) {
        // no-op
      }

      @Override
      public void handleEndTag(final HTML.Tag t, final int pos) {
        // no-op
      }

      @Override
      public void handleSimpleTag(final HTML.Tag t, final MutableAttributeSet a, final int pos) {
        if (HTML.Tag.BR.equals(t)) {
          builder.append('\n');
        }
      }

      @Override
      public void handleComment(final char[] data, final int pos) {
        // no-op
      }

      @Override
      public void handleError(final String errMsg, final int pos) {
        // no-op
      }
    };
    try {
      new ParserDelegator().parse(reader, parserCallback, true);
    } catch (IOException e) {
      throw new RuntimeException("Failed to parse HTML content", e);
    }
    return builder.toString();
  }