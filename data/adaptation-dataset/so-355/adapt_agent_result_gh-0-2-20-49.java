private void initializeActionBar(final PreferenceScreen preferenceScreen) {
	if (preferenceScreen == null) {
		return;
	}

	final Dialog dialog = preferenceScreen.getDialog();
	if (dialog == null) {
		return;
	}

	// Only operate on the existing home view if present; do not configure the ActionBar
	final View homeBtn = dialog.findViewById(android.R.id.home);
	if (homeBtn == null) {
		return;
	}

	final View.OnClickListener dismissDialogClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			dialog.dismiss();
		}
	};

	// Prepare yourselves for some hacky programming
	final ViewParent homeBtnContainer = homeBtn.getParent();

	// The home button is an ImageView inside a FrameLayout
	if (homeBtnContainer instanceof FrameLayout) {
		final ViewParent containerParent = homeBtnContainer.getParent();
		if (containerParent instanceof LinearLayout) {
			// This view also contains the title text, set the whole view as clickable
			((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
		} else {
			// Just set it on the home button container
			((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
		}
	} else if (homeBtnContainer instanceof ViewGroup) {
		// Fallback: attach to the immediate container when possible
		((ViewGroup) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
	} else {
		// The 'if all else fails' default case
		homeBtn.setOnClickListener(dismissDialogClickListener);
	}
}