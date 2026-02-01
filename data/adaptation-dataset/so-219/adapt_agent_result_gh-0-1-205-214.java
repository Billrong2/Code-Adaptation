public static Bitmap setHasAlphaCompat(Bitmap bit) {
    // Reference: StackOverflow answer (SO ID: to be filled later)
    int width = bit.getWidth();
    int height = bit.getHeight();
    Bitmap myBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    int[] allPixels = new int[myBitmap.getHeight() * myBitmap.getWidth()];
    bit.getPixels(allPixels, 0, myBitmap.getWidth(), 0, 0, myBitmap.getWidth(), myBitmap.getHeight());
    myBitmap.setPixels(allPixels, 0, width, 0, 0, width, height);
    return myBitmap;
}