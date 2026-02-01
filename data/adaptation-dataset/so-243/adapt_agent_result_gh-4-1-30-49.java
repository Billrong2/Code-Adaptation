    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (g == null || _hint == null || !isEnabled()) {
            return;
        }
        String text = getText();
        if (text != null && text.length() > 0) {
            return;
        }

        // Use hint font if provided
        Font hintFont = _font != null ? _font : getFont();
        if (hintFont == null) {
            return;
        }
        g.setFont(hintFont);

        // Enable text antialiasing when possible
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }

        Insets insets = getInsets();
        FontMetrics fontMetrics = g.getFontMetrics();
        int height = getHeight();

        // Blend background and foreground colors (preserve existing logic)
        final int BLEND_MASK = 0xfefefefe;
        int bgRgb = getBackground().getRGB();
        int fgRgb = getForeground().getRGB();
        int blendedRgb = ((bgRgb & BLEND_MASK) >>> 1) + ((fgRgb & BLEND_MASK) >>> 1);
        g.setColor(new Color(blendedRgb, true));

        // Compute vertical adjustment only for the designated hint font
        final int BASELINE_ADJUST = 2;
        int adjustY = hintFont.equals(_font) ? (BASELINE_ADJUST + adjustOS) : 0;

        int y = height / 2 + fontMetrics.getAscent() / 2 - BASELINE_ADJUST + adjustY;
        g.drawString(_hint, insets.left, y);
    }