	/**
	 * Returns the path of one {@link File} relative to another.
	 * <p/>
	 * Adapted from a Stack Overflow answer. The {@code baseDirectory} parameter is
	 * treated as a directory when computing the relative path.
	 *
	 * @param target
	 *            the target file or directory for which a relative path is
	 *            calculated
	 * @param baseDirectory
	 *            the base directory from which the relative path is calculated
	 * @return a {@link File} representing {@code target}'s path relative to the
	 *         {@code baseDirectory}
	 * @throws IOException
	 *             if an error occurs while resolving the canonical paths of the
	 *             files
	 */
	public static File getRelativeFile(final File target, final File baseDirectory) throws IOException
	{
		final String[] baseComponents = baseDirectory.getCanonicalPath()
				.split(Pattern.quote(File.separator));
		final String[] targetComponents = target.getCanonicalPath()
				.split(Pattern.quote(File.separator));

		// skip common components
		int index = 0;
		for (; index < targetComponents.length && index < baseComponents.length; ++index)
		{
			if (!targetComponents[index].equals(baseComponents[index]))
			{
				break;
			}
		}

		final StringBuilder result = new StringBuilder();
		if (index != baseComponents.length)
		{
			// backtrack to base directory
			for (int i = index; i < baseComponents.length; ++i)
			{
				result.append(".." + File.separator);
			}
		}
		for (; index < targetComponents.length; ++index)
		{
			result.append(targetComponents[index] + File.separator);
		}
		if (!target.getPath().endsWith("/") && !target.getPath().endsWith("\\"))
		{
			// remove final path separator
			result.delete(result.length() - File.separator.length(), result.length());
		}
		return new File(result.toString());
	}