private static void addFileToZip(String path, String srcFile, java.util.zip.ZipOutputStream zip, boolean flag) throws Exception {
      if (srcFile == null || zip == null) {
         return;
      }
      /*
       * create the file object for inputs
       */
      java.io.File folder = new java.io.File(srcFile);

      /*
       * if the folder is empty add empty folder to the Zip file
       */
      if (flag == true) {
         zip.putNextEntry(new java.util.zip.ZipEntry(path + "/" + folder.getName() + "/"));
      }
      else {
         /*
          * if the current name is directory, recursively traverse it
          * to get the files
          */
         if (folder.isDirectory()) {
            /*
             * if folder is not empty
             */
            addFolderToZip(path, srcFile, zip);
         }
         else {
            /*
             * write the file to the output
             */
            byte[] buf = new byte[1024];
            int len;
            zip.putNextEntry(new java.util.zip.ZipEntry(path + "/" + folder.getName()));
            try (java.io.FileInputStream in = new java.io.FileInputStream(srcFile)) {
               while ((len = in.read(buf)) > 0) {
                  /*
                   * Write the Result
                   */
                  zip.write(buf, 0, len);
               }
            }
         }
      }
   }