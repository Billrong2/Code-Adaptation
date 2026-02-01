public TestFrame() {
		super();
		setSize(500, 500);
		getContentPane().setLayout(new BorderLayout());
		final JCheckBoxTree cbt = new JCheckBoxTree();
		getContentPane().add(cbt, BorderLayout.CENTER);
		cbt.addCheckChangeEventListener(new JCheckBoxTree.CheckChangeEventListener() {
			@Override
			public void checkStateChanged(JCheckBoxTree.CheckChangeEvent event) {
				System.out.println("event");
				TreePath[] paths = cbt.getCheckedPaths();
				if (paths != null) {
					for (TreePath tp : paths) {
						for (Object pathPart : tp.getPath()) {
							System.out.print(pathPart + ",");
						}
						System.out.println();
					}
				}
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}