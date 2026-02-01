private StringBuffer createStyle() {
    // for copying style
    final JLabel label = new JLabel();
    final Font font = label.getFont();
    final Color color = label.getBackground();

    // create some css from the label's font
    final StringBuffer css = new StringBuffer("font-family:" + font.getFamily() + ";");
    css.append("font-weight:" + (font.isBold() ? "bold" : "normal") + ";");
    css.append("font-size:" + font.getSize() + "pt;");
    css.append("background-color: rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ");");
    return css;
}