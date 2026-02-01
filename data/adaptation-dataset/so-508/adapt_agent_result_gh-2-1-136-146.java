@Override
public Component getListCellRendererComponent(final JList list, final Object value, final int index,
        final boolean isSelected, final boolean cellHasFocus) {
    if (!(value instanceof JCheckBox)) {
        return new JCheckBox();
    }

    final JCheckBox checkbox = (JCheckBox) value;

    // Selection-based appearance
    checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
    checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
    checkbox.setFont(getFont());

    // Do not sync enabled state with renderer
    checkbox.setFocusPainted(false);

    // Disable checkbox button border painting; rely on component border
    checkbox.setBorderPainted(false);

    // Border handling: focus/highlight when selected, simple empty border otherwise
    if (isSelected) {
        checkbox.setBorder(UIManager.getBorder("List.focusCellHighlightBorder"));
    } else {
        checkbox.setBorder(new EmptyBorder(1, 1, 1, 1));
    }

    return checkbox;
}