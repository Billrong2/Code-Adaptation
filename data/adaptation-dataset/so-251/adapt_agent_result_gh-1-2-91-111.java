@Override
public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
	android.view.View rowView = convertView;
	MenuItemHolder holder;

	// basic hardening checks
	if (items == null || position < 0 || position >= items.length) {
		return rowView;
	}

	if (rowView == null) {
		android.view.LayoutInflater inflater = act.getLayoutInflater();
		rowView = inflater.inflate(de.meisterfuu.animexx.R.layout.slidemenu_listitem, parent, false);
		holder = new MenuItemHolder();
		holder.label = (android.widget.TextView) rowView.findViewById(de.meisterfuu.animexx.R.id.menu_label);
		holder.icon = (android.widget.ImageView) rowView.findViewById(de.meisterfuu.animexx.R.id.menu_icon);
		rowView.setTag(holder);
	} else {
		holder = (MenuItemHolder) rowView.getTag();
	}

	SlideMenuItem item = items[position];
	if (item == null || holder == null) {
		return rowView;
	}

	// set label text
	String labelText = item.label;
	if (holder.label != null) {
		holder.label.setText(labelText);
		if (itemFont != null) {
			holder.label.setTypeface(itemFont);
		}
	}

	// set icon drawable
	if (holder.icon != null) {
		holder.icon.setImageDrawable(item.icon);
	}

	return rowView;
}