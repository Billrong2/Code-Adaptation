protected static Bitmap bitmapForUri(Context context, Uri imageUri) throws IOException {
	if (imageUri == null) {
		return BitmapFactory.decodeResource(context.getResources(), edu.northwestern.cbits.purple.notifier.R.drawable.ic_launcher);
	}

	InputStream input = null;

	try {
		input = PurpleWidgetProvider.inputStreamForUri(context, imageUri);

		if (input == null)
			return null;

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
	} finally {
		if (input != null)
			input.close();
	}

	if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
		return null;

	int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

	double ratio = (originalSize > 144) ? (originalSize / 144.0) : 1.0;

	BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	bitmapOptions.inSampleSize = PurpleWidgetProvider.getPowerOfTwoForSampleRatio(ratio);
	bitmapOptions.inDither = true;
	bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

	try {
		input = PurpleWidgetProvider.inputStreamForUri(context, imageUri);

		if (input == null)
			return null;

		return BitmapFactory.decodeStream(input, null, bitmapOptions);
	} finally {
		if (input != null)
			input.close();
	}
}