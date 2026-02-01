private void loadFileList(File path) {
    // Guard against null path
    if (path == null) {
        this.currentPath = null;
        this.fileList = new String[]{};
        return;
    }

    this.currentPath = path;
    List<String> results = new ArrayList<String>();

    if (path.exists()) {
        if (path.getParentFile() != null) {
            results.add(PARENT_DIR);
        }

        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                File sel = new File(dir, filename);
                if (!sel.canRead()) return false;

                if (selectDirectoryOption) {
                    return sel.isDirectory();
                }

                // If fileEndsWith is null, allow all files; always allow directories
                boolean allowFile = (fileEndsWith == null) || filename.toLowerCase().endsWith(fileEndsWith);
                return sel.isDirectory() || allowFile;
            }
        };

        String[] listedFiles = path.list(filter);
        if (listedFiles != null) {
            for (String file : listedFiles) {
                results.add(file);
            }
        }
    }

    this.fileList = results.toArray(new String[]{});
}