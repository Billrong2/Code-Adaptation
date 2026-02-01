public void handleMediaKeyEvent(final android.view.KeyEvent keyEvent) {
	// Uses reflection to access hidden Android audio service APIs and dispatch media key events.
	// Adapted from a Stack Overflow example; relies on unsupported/hidden APIs and may break across Android versions.
	if (keyEvent == null) {
		android.util.Log.w("media", "handleMediaKeyEvent called with null KeyEvent");
		return;
	}
	try {
		// Obtain the audio service binder via ServiceManager.checkService(Context.AUDIO_SERVICE)
		final Object audioServiceBinderObj = java.lang.Class.forName("android.os.ServiceManager")
				.getDeclaredMethod("checkService", java.lang.String.class)
				.invoke(null, android.content.Context.AUDIO_SERVICE);
		if (!(audioServiceBinderObj instanceof android.os.IBinder)) {
			android.util.Log.w("media", "Audio service binder unavailable");
			return;
		}
		final android.os.IBinder audioServiceBinder = (android.os.IBinder) audioServiceBinderObj;

		// Resolve IAudioService from the binder
		final Object audioService = java.lang.Class.forName("android.media.IAudioService$Stub")
				.getDeclaredMethod("asInterface", android.os.IBinder.class)
				.invoke(null, audioServiceBinder);
		if (audioService == null) {
			android.util.Log.w("media", "IAudioService unavailable");
			return;
		}

		// Dispatch the media key event
		java.lang.Class.forName("android.media.IAudioService")
				.getDeclaredMethod("dispatchMediaKeyEvent", android.view.KeyEvent.class)
				.invoke(audioService, keyEvent);
	} catch (java.lang.ClassNotFoundException e) {
		android.util.Log.e("media", "Required hidden class not found while dispatching media key", e);
	} catch (java.lang.NoSuchMethodException e) {
		android.util.Log.e("media", "Required hidden method not found while dispatching media key", e);
	} catch (java.lang.IllegalAccessException e) {
		android.util.Log.e("media", "Illegal access while dispatching media key", e);
	} catch (java.lang.reflect.InvocationTargetException e) {
		android.util.Log.e("media", "Invocation target exception while dispatching media key", e);
	}
}