/**
     * Zips the contents of the given directory into the given zip file, preserving
     * directory structure and traversal behavior.
     *
     * @author Ilias Tsagklis
     * @param directory
     *            - The directory whose contents should be zipped
     * @param zipfile
     *            - The destination zip file
     * @throws IOException
     *             if an I/O error occurs during zipping
     */
    @SuppressWarnings("resource")
    public void zipFolderContents(File directory, File zipfile) throws IOException
    {
        if (directory == null || zipfile == null)
        {
            throw new IllegalArgumentException("directory and zipfile must not be null");
        }

        final URI base = directory.toURI();
        final Deque<File> queue = new LinkedList<File>();
        queue.push(directory);

        OutputStream out = new FileOutputStream(zipfile);
        Closeable res = out;

        try
        {
            ZipOutputStream zout = new ZipOutputStream(out);
            res = zout;

            while (!queue.isEmpty())
            {
                File current = queue.pop();
                File[] children = current.listFiles();
                if (children == null)
                {
                    continue;
                }

                for (File child : children)
                {
                    String name = base.relativize(child.toURI()).getPath();

                    if (child.isDirectory())
                    {
                        queue.push(child);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    }
                    else
                    {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(child, zout);
                        zout.closeEntry();
                    }
                }
            }
        }
        finally
        {
            res.close();
        }
    }