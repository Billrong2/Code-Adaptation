private void playMP3(final String url) {
	// JavaFX-based asynchronous MP3 playback from a full URL
	if (url == null || url.trim().isEmpty()) {
		Log.debug("playMP3 called with null or empty url");
		return;
	}

	try {
		// Ensure JavaFX runtime is initialized (safe to call multiple times)
		new JFXPanel();

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					Media media = new Media(url);
					final MediaPlayer player = new MediaPlayer(media);

					player.setOnError(new Runnable() {
						@Override
						public void run() {
							Log.debug("MediaPlayer error while playing MP3: "
									+ player.getError());
							try {
								player.stop();
								hplayerDispose(player);
							} catch (Exception e) {
								// ignore cleanup errors
							}
						}
					});

					player.play();
				} catch (javafx.scene.media.MediaException me) {
					Log.debug("Failed to initialize MediaPlayer for URL: " + url
							+ ", error=" + me.getMessage());
				}
			}
		});
	} catch (Exception e) {
		Log.debug("Unexpected error initializing JavaFX MP3 playback: "
				+ e.getMessage());
	}

	// Deprecated fallback: old AudioSystem/Decoder-based streaming intentionally omitted
}
