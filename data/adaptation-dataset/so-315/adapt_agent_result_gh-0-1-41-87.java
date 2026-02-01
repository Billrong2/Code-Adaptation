public TypedComboBox(org.eclipse.swt.widgets.Composite parent) {
	this.viewer = new org.eclipse.jface.viewers.ComboViewer(parent, org.eclipse.swt.SWT.READ_ONLY);
	this.viewer.setContentProvider(org.eclipse.jface.viewers.ArrayContentProvider.getInstance());

	this.viewer.setLabelProvider(new org.eclipse.jface.viewers.LabelProvider() {
		@Override
		public String getText(Object element) {
			T typedElement = getTypedObject(element);
			if (labelProvider != null && typedElement != null) {
				if (typedElement == currentSelection) {
					return labelProvider.getSelectedLabel(typedElement);
				} else {
					return labelProvider.getListLabel(typedElement);
				}
			}
			return element != null ? element.toString() : "";
		}

		@Override
		public org.eclipse.swt.graphics.Image getImage(Object element) {
			T typedElement = getTypedObject(element);
			if (labelProvider != null && typedElement != null) {
				return labelProvider.getImage(typedElement);
			}
			return null;
		}
	});

	this.viewer.addSelectionChangedListener(new org.eclipse.jface.viewers.ISelectionChangedListener() {
		@Override
		public void selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent event) {
			org.eclipse.jface.viewers.IStructuredSelection selection = (org.eclipse.jface.viewers.IStructuredSelection) event.getSelection();
			T typedSelection = getTypedObject(selection.getFirstElement());
			if (typedSelection != null) {
				currentSelection = typedSelection;
				viewer.refresh();
				notifySelectionListeners(typedSelection);
			}
		}
	});

	this.content = new java.util.ArrayList<T>();
	this.selectionListeners = new java.util.ArrayList<TypedComboBoxSelectionListener<T>>();
}