public List<String> getResourcesFromDirectory(File rootDir, Pattern pattern) {
        List<String> results = new ArrayList<>();
        if (rootDir == null || pattern == null) {
            return results;
        }
        if (!rootDir.exists() || !rootDir.isDirectory() || !rootDir.canRead()) {
            log.warn("Cannot scan directory: {}", rootDir);
            return results;
        }
        // Track visited canonical paths to avoid infinite recursion (e.g., via symlinks)
        java.util.Set<String> visited = new java.util.HashSet<>();
        java.util.ArrayDeque<File> stack = new java.util.ArrayDeque<>();
        stack.push(rootDir);
        while (!stack.isEmpty()) {
            File current = stack.pop();
            String canonicalPath;
            try {
                canonicalPath = current.getCanonicalPath();
            } catch (IOException ioe) {
                log.warn("Failed to resolve canonical path for {}", current, ioe);
                continue;
            }
            if (!visited.add(canonicalPath)) {
                continue; // already visited
            }
            File[] children;
            try {
                children = current.listFiles();
            } catch (SecurityException se) {
                log.warn("Access denied while listing directory {}", current, se);
                continue;
            }
            if (children == null) {
                continue;
            }
            for (File child : children) {
                if (child == null) {
                    continue;
                }
                try {
                    if (child.isDirectory()) {
                        // Avoid following symbolic links to directories
                        if (!java.nio.file.Files.isSymbolicLink(child.toPath())) {
                            stack.push(child);
                        }
                    } else if (child.isFile()) {
                        String fileCanonicalPath = child.getCanonicalPath();
                        boolean accept = pattern.matcher(fileCanonicalPath).matches();
                        if (accept) {
                            results.add(fileCanonicalPath);
                        }
                    }
                } catch (IOException ioe) {
                    log.warn("I/O error processing file {}", child, ioe);
                    // continue with remaining files
                }
            }
        }
        return results;
    }