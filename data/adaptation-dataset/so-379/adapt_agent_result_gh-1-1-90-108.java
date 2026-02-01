private void openLink() {
    // Do nothing if no URL is set
    if (url == null) {
        return;
    }
    if (Desktop.isDesktopSupported()) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(url);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Failed to launch the link, your computer is likely misconfigured.",
                "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(null,
            "Java is not able to launch links on your computer.",
            "Cannot Launch Link", JOptionPane.WARNING_MESSAGE);
    }
}