public static void initializeActionBar(final PreferenceScreen preferenceScreen) {
	if (preferenceScreen == null) {
		return;
	}

	final Dialog dialog = preferenceScreen.getDialog();
	if (dialog == null || dialog.getActionBar() == null) {
		return;
	}

	// Initialize the action bar only when present
	dialog.getActionBar().setDisplayHomeAsUpEnabled(true);

	// Apply custom home button area click listener to close the PreferenceScreen
	// because PreferenceScreens are dialogs which swallow events instead of passing to the activity
	View homeBtn = dialog.findViewById(android.R.id.home);
	if (homeBtn != null) {
		OnClickListener dismissDialogClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		};

		ViewParent homeBtnContainer = homeBtn.getParent();
		// The home button is an ImageView inside a FrameLayout
		if (homeBtnContainer instanceof FrameLayout) {
			ViewGroup containerParent = (ViewGroup) homeBtnContainer.getParent();
			if (containerParent instanceof LinearLayout) {
				// This view also contains the title text, set the whole view as clickable
				((LinearLayout) containerParent).setOnClickListener(dismissDialogClickListener);
			}
			else {
				// Just set it on the home button container
				((FrameLayout) homeBtnContainer).setOnClickListener(dismissDialogClickListener);
			}
		}
		else {
			// Fallback case
			homeBtn.setOnClickListener(dismissDialogClickListener);
		}
	}
}