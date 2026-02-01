private boolean unpackZip(String presetDir, String zipName) {
	File targetDir = new File(presetDir);
	if (!targetDir.exists() && !targetDir.mkdirs()) {
		Log.w("PresetEditorActivity", "Could not create preset directory " + targetDir.getAbsolutePath());
		return false;
	}
	byte[] buffer = new byte[8192];
	try (InputStream is = new FileInputStream(new File(targetDir, zipName));
		 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is))) {
		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			File outFile = new File(targetDir, ze.getName());
			// Prevent Zip Slip
			String canonicalTargetDir = targetDir.getCanonicalPath();
			String canonicalOutFile = outFile.getCanonicalPath();
			if (!canonicalOutFile.startsWith(canonicalTargetDir + File.separator)) {
				Log.w("PresetEditorActivity", "Skipping suspicious zip entry " + ze.getName());
				zis.closeEntry();
				continue;
			}
			if (ze.isDirectory()) {
				if (!outFile.exists() && !outFile.mkdirs()) {
					Log.w("PresetEditorActivity", "Could not create directory " + outFile.getAbsolutePath());
					return false;
				}
				zis.closeEntry();
				continue;
			}
			File parent = outFile.getParentFile();
			if (parent != null && !parent.exists() && !parent.mkdirs()) {
				Log.w("PresetEditorActivity", "Could not create directory " + parent.getAbsolutePath());
				return false;
			}
			try (FileOutputStream fout = new FileOutputStream(outFile)) {
				int count;
				while ((count = zis.read(buffer)) != -1) {
					fout.write(buffer, 0, count);
				}
			}
			zis.closeEntry();
		}
		return true;
	} catch (IOException e) {
		Log.w("PresetEditorActivity", "Error unpacking zip " + zipName, e);
		return false;
	}
}