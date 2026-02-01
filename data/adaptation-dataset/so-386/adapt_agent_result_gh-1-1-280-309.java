public static void main(String[] args) {
	// Create and show the UI on the Swing event dispatch thread
	javax.swing.SwingUtilities.invokeLater(new Runnable() {
		@Override
		public void run() {
			javax.swing.JFrame frame = new javax.swing.JFrame("JCheckBoxTree Demo");
			frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
			frame.setSize(500, 500);
			frame.getContentPane().setLayout(new java.awt.BorderLayout());

			final com.opendoorlogistics.codefromweb.JCheckBoxTree cbt = new com.opendoorlogistics.codefromweb.JCheckBoxTree();
			frame.getContentPane().add(cbt, java.awt.BorderLayout.CENTER);

			cbt.addCheckChangeEventListener(new com.opendoorlogistics.codefromweb.JCheckBoxTree.CheckChangeEventListener() {
				@Override
				public void checkStateChanged(com.opendoorlogistics.codefromweb.JCheckBoxTree.CheckChangeEvent event) {
					System.out.println("event");
					javax.swing.tree.TreePath[] paths = cbt.getCheckedPaths();
					for (javax.swing.tree.TreePath tp : paths) {
						for (Object pathPart : tp.getPath()) {
							System.out.print(pathPart + ",");
						}
						System.out.println();
					}
				}
			});

			frame.setVisible(true);
		}
	});
}