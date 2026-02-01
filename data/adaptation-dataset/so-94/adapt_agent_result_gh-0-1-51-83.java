private static void notSoUglyPlease(org.jfree.chart.JFreeChart chart) {
	// code hardening: null checks
	if (chart == null) {
		return;
	}

	org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
	if (plot == null) {
		return;
	}

	String fontName = "Lucida Sans";

	// apply a standard theme with customized typography and colors
	org.jfree.chart.StandardChartTheme theme = (org.jfree.chart.StandardChartTheme) org.jfree.chart.StandardChartTheme.createJFreeTheme();
	theme.setTitlePaint(java.awt.Color.decode("#4572a7"));
	theme.setExtraLargeFont(new java.awt.Font(fontName, java.awt.Font.BOLD, 14)); // title (adjusted)
	theme.setLargeFont(new java.awt.Font(fontName, java.awt.Font.BOLD, 15)); // axis-title
	theme.setRegularFont(new java.awt.Font(fontName, java.awt.Font.PLAIN, 11));
	theme.setRangeGridlinePaint(java.awt.Color.decode("#C0C0C0"));
	theme.setPlotBackgroundPaint(java.awt.Color.white);
	theme.setChartBackgroundPaint(java.awt.Color.white);
	theme.setGridBandPaint(java.awt.Color.red);
	theme.setAxisOffset(new org.jfree.ui.RectangleInsets(0, 0, 0, 0));
	theme.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());
	theme.setAxisLabelPaint(java.awt.Color.decode("#666666"));
	theme.apply(chart);

	// plot and axis tweaks
	plot.setOutlineVisible(false);
	if (plot.getRangeAxis() != null) {
		plot.getRangeAxis().setAxisLineVisible(false);
		plot.getRangeAxis().setTickMarksVisible(false);
		plot.getRangeAxis().setTickLabelPaint(java.awt.Color.decode("#666666"));
	}
	if (plot.getDomainAxis() != null) {
		plot.getDomainAxis().setTickLabelPaint(java.awt.Color.decode("#666666"));
	}
	plot.setRangeGridlineStroke(new java.awt.BasicStroke());

	// rendering hints
	chart.setTextAntiAlias(true);
	chart.setAntiAlias(true);

	// renderer-specific settings (guarded)
	org.jfree.chart.renderer.category.CategoryItemRenderer renderer = plot.getRenderer();
	if (renderer instanceof org.jfree.chart.renderer.category.BarRenderer) {
		org.jfree.chart.renderer.category.BarRenderer barRenderer = (org.jfree.chart.renderer.category.BarRenderer) renderer;
		barRenderer.setShadowVisible(true);
		barRenderer.setShadowXOffset(2);
		barRenderer.setShadowYOffset(0);
		barRenderer.setShadowPaint(java.awt.Color.decode("#C0C0C0"));
		barRenderer.setMaximumBarWidth(0.1);
	}
}