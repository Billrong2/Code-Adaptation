/**
 * Downloads a file from a remote URL into a local destination file.
 * 
 * @author unknown
 * @origin StackOverflow
 * @license unknown
 * @retrieved unknown
 */
public static void downloadFile(File destination, String URL) {
    try {
        BufferedInputStream in = new BufferedInputStream(new java.net.URL(URL).openStream());
        FileOutputStream fout = new FileOutputStream(destination);

        final byte data[] = new byte[1024];
        int count;
        while ((count = in.read(data, 0, 1024)) != -1) {
            fout.write(data, 0, count);
        }

        in.close();
        fout.close();
    } catch (Exception e) {
    }
}