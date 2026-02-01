private void logVersionInfo()
{
	try
	{
		final Enumeration<URL> manifestResources = Thread.currentThread()
				.getContextClassLoader()
				.getResources(JarFile.MANIFEST_NAME);

		while (manifestResources.hasMoreElements())
		{
			final URL manifestUrl = manifestResources.nextElement();
			try (final InputStream inputStream = manifestUrl.openStream())
			{
				if (inputStream == null)
				{
					continue;
				}

				final Manifest manifest = new Manifest(inputStream);
				final Attributes mainAttributes = manifest.getMainAttributes();
				final String version = mainAttributes.getValue("Implementation-Version");

				if (version != null)
				{
					logger.log(Level.INFO, "Found Implementation-Version {0} in manifest {1}", new Object[] { version, manifestUrl });
				}
			}
			catch (final Exception e)
			{
				// Silently ignore wrong manifests on classpath
			}
		}
	}
	catch (final IOException e)
	{
		// Silently ignore wrong manifests on classpath
	}
}