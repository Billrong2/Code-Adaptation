public static void packColumn(JTable table, int vColIndex, int margin, int minWidth) {
	// Null-safety and basic validation
	if (table == null) {
		return;
	}
	TableColumnModel columnModel = table.getColumnModel();
	if (columnModel == null || vColIndex < 0 || vColIndex >= columnModel.getColumnCount()) {
		return;
	}

	TableColumn column = columnModel.getColumn(vColIndex);
	int width = 0;

	// Calculate header width if possible (null-safe)
	javax.swing.table.JTableHeader header = table.getTableHeader();
	TableCellRenderer headerRenderer = column.getHeaderRenderer();
	if (headerRenderer == null && header != null) {
		headerRenderer = header.getDefaultRenderer();
	}
	if (headerRenderer != null) {
		java.awt.Component headerComp = headerRenderer.getTableCellRendererComponent(
			table,
			column.getHeaderValue(),
			false,
			false,
			0,
			vColIndex);
		if (headerComp != null) {
			width = headerComp.getPreferredSize().width;
		}
	}

	// Calculate maximum cell width with performance guard (max 1000 rows)
	int rowCount = table.getRowCount();
	int maxRowsToScan = Math.min(rowCount, 1000);
	for (int row = 0; row < maxRowsToScan; row++) {
		TableCellRenderer cellRenderer = table.getCellRenderer(row, vColIndex);
		if (cellRenderer == null) {
			continue;
		}
		java.awt.Component cellComp = cellRenderer.getTableCellRendererComponent(
			table,
			table.getValueAt(row, vColIndex),
			false,
			false,
			row,
			vColIndex);
		if (cellComp != null) {
			width = Math.max(width, cellComp.getPreferredSize().width);
		}
	}

	// Add margins and enforce minimum width
	width += 2 * margin;
	width = Math.max(width, minWidth);

	// Apply preferred width
	column.setPreferredWidth(width);
}