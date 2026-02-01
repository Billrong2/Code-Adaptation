/**
     * Discovers external storage mount paths by parsing the output of the system "mount" command.
     * <p>
     * The returned list preserves discovery (insertion) order and may contain duplicates. This allows
     * callers to reliably select "the first" mounted SD card path when multiple mounts are present.
     * </p>
     *
     * @return An ordered {@link java.util.ArrayList} of mount paths (possibly empty, never null).
     */
    public static ArrayList<String> getExternalMounts() {
        final ArrayList<String> mounts = new ArrayList<String>();
        final String mountRegex = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        final String mountCommand = "mount";

        Process process = null;
        try {
            process = new ProcessBuilder().command(mountCommand)
                    .redirectErrorStream(true)
                    .start();

            // Read process output safely and in order
            try (java.io.InputStream is = process.getInputStream();
                 java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Skip asec mounts
                    if (line.toLowerCase(Locale.US).contains("asec")) {
                        continue;
                    }

                    if (line.matches(mountRegex)) {
                        String[] parts = line.split(" ");
                        for (String part : parts) {
                            if (part != null && part.startsWith("/")
                                    && !part.toLowerCase(Locale.US).contains("vold")) {
                                mounts.add(part);
                            }
                        }
                    }
                }
            }

            try {
                process.waitFor();
            } catch (InterruptedException ie) {
                // Restore interrupt status and continue with whatever data was read
                Thread.currentThread().interrupt();
            }
        } catch (IOException ioe) {
            Log.e(LOG_TOKEN, "Error while discovering external mounts", ioe);
        }

        return mounts;
    }