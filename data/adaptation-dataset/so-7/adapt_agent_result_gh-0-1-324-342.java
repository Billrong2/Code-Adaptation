private void initInfoPerResource(int row, int col) {
	// Reset UI info collections
	colours.clear();
	tooltips.clear();

	// Basic validation
	if (model == null || model.getRowKeys() == null) {
		return;
	}
	if (row < 0 || row >= model.getRowKeys().size()) {
		return;
	}

	Object rowKey = model.getRowKeys().get(row);
	if (!(rowKey instanceof TaskSeries)) {
		return;
	}

	TaskSeries series = (TaskSeries) rowKey;
	@SuppressWarnings("unchecked")
	List<Task> tasks = series.getTasks();
	if (tasks == null || col < 0 || col >= tasks.size()) {
		return;
	}

	Task task = tasks.get(col);
	if (task == null || task.getSubtasks() == null || task.getSubtasks().isEmpty()) {
		return;
	}

	// Iterate real subtasks and build colours + tooltips in parallel
	for (Task sub : task.getSubtasks()) {
		if (!(sub instanceof MySubtask)) {
			continue;
		}
		MySubtask mySubtask = (MySubtask) sub;
		GanttItem item = mySubtask.getItem();
		if (item == null) {
			continue;
		}

		// Resolve colour by activity id with safe fallback
		Color colour = null;
		if (colourMap != null && item.getActivityId() != null) {
			colour = colourMap.get(item.getActivityId());
		}
		if (colour == null) {
			colour = Color.GRAY;
		}
		colours.add(colour);

		// Tooltip uses the item name where available
		String tooltip = item.getName();
		tooltips.add(tooltip != null ? tooltip : "");
	}
}