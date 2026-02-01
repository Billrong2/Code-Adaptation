public static Set<String> getExternalMounts() {
    final Set<String> mounts = new HashSet<String>();
    try {
        // Constants for command and parsing
        final String mountCommand = "mount";
        final String mountLineRegex = ".*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*"; // case-sensitive
        final StringBuilder mountOutput = new StringBuilder();

        Process process = null;
        InputStream inputStream = null;
        try {
            process = new ProcessBuilder().command(mountCommand).redirectErrorStream(true).start();
            process.waitFor();
            inputStream = process.getInputStream();
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                mountOutput.append(new String(buffer, 0, read));
            }
        } catch (Exception ex) {
            error(TAG, "Error executing mount command", ex);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception ex) {
                error(TAG, "Error closing mount command stream", ex);
            }
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception ignore) {
                    // ignore
                }
            }
        }

        // Guard against empty output
        if (mountOutput.length() == 0) {
            return mounts;
        }

        // Parse output
        final String[] lines = mountOutput.toString().split("\n");
        for (final String line : lines) {
            if (line == null) {
                continue;
            }
            final String lowerLine = line.toLowerCase(Locale.ENGLISH);
            if (lowerLine.contains("asec")) {
                continue;
            }
            if (line.matches(mountLineRegex)) {
                final String[] parts = line.split(" ");
                for (final String part : parts) {
                    if (part != null && part.startsWith("/") && !part.toLowerCase(Locale.ENGLISH).contains("vold")) {
                        mounts.add(part);
                        debug(TAG, "External mount found: %s", part);
                    }
                }
            }
        }
    } catch (Exception ex) {
        error(TAG, "Unexpected error while detecting external mounts", ex);
    }
    return mounts;
}