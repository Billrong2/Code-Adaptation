private boolean showChooser()
{
	if(getActivity() == null)
		return false;

	// Prepare file/URI for potential camera capture
	if(!prepareFile() || photoFileUri == null)
		return false;

	final android.content.pm.PackageManager pm = getActivity().getPackageManager();
	if(pm == null)
		return false;

	// Camera intents
	final java.util.List<android.content.Intent> cameraIntents = new java.util.ArrayList<>();
	final android.content.Intent captureIntent = new android.content.Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	final java.util.List<android.content.pm.ResolveInfo> camActivities = pm.queryIntentActivities(captureIntent, 0);
	if(camActivities != null)
	{
		for(android.content.pm.ResolveInfo res : camActivities)
		{
			final android.content.Intent intent = new android.content.Intent(captureIntent);
			intent.setComponent(new android.content.ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			intent.setPackage(res.activityInfo.packageName);
			intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoFileUri);
			intent.putExtra("imagePicker_cameraCapture", true);
			cameraIntents.add(intent);
		}
	}

	// Base chooser intent: GET_CONTENT
	final android.content.Intent baseIntent = new android.content.Intent(android.content.Intent.ACTION_GET_CONTENT);
	baseIntent.setType("image/*");

	// Additional gallery option: ACTION_PICK
	final java.util.List<android.content.Intent> extraIntents = new java.util.ArrayList<>();
	final android.content.Intent pickIntent = new android.content.Intent(android.content.Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	pickIntent.setType("image/*");
	extraIntents.add(pickIntent);

	// Merge camera intents
	if(!cameraIntents.isEmpty())
		extraIntents.addAll(cameraIntents);

	final android.content.Intent chooser = android.content.Intent.createChooser(baseIntent, getTitle());
	if(!extraIntents.isEmpty())
	{
		chooser.putExtra(android.content.Intent.EXTRA_INITIAL_INTENTS, extraIntents.toArray(new android.os.Parcelable[extraIntents.size()]));
	}

	startActivityForResult(chooser, REQUEST_TAKE_PHOTO);
	return true;
}