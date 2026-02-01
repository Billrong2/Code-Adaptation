/**
	 * Returns a set of external storage mount points by parsing the output of the
	 * platform "mount" command. This relies on the command's textual output format
	 * and may not work uniformly across all Android versions or devices.
	 */
	public static Set<String> getExternalMounts()
	{
		final Set<String> mounts = new HashSet<>();

		// Regex and command preserved exactly from original logic
		final String MOUNT_COMMAND = "mount";
		final String MOUNT_LINE_REGEX = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";

		final StringBuilder outputBuilder = new StringBuilder();

		try
		{
			final Process process = new ProcessBuilder().command(MOUNT_COMMAND)
					.redirectErrorStream(true)
					.start();

			// Wait for process to complete
			process.waitFor();

			// Read process output safely
			try (InputStream inputStream = process.getInputStream())
			{
				final byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1)
				{
					outputBuilder.append(new String(buffer, 0, bytesRead));
				}
			}
		}
		catch (IOException e)
		{
			// I/O failure while executing or reading from mount command
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			// Restore interrupted status and continue with whatever output was collected
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}

		final String output = outputBuilder.toString();
		if (output.isEmpty())
		{
			return mounts;
		}

		// Parse mount output
		final String[] lines = output.split("\n");
		for (final String line : lines)
		{
			if (!line.toLowerCase(Locale.US).contains("asec"))
			{
				if (line.matches(MOUNT_LINE_REGEX))
				{
					final String[] parts = line.split(" ");
					for (final String part : parts)
					{
						if (part.startsWith("/"))
						{
							if (!part.toLowerCase(Locale.US).contains("vold"))
							{
								mounts.add(part);
							}
						}
					}
				}
			}
		}

		return mounts;
	}