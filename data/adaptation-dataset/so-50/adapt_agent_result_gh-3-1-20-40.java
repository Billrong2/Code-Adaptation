public static OSType getOperatingSystemType() {
    if (detectedOS == null) {
      String osName = System.getProperty("os.name", "generic").toLowerCase();
      if (osName.indexOf("mac") >= 0 || osName.indexOf("darwin") >= 0) {
        detectedOS = OSType.MacOS;
      } else if (osName.indexOf("win") >= 0) {
        detectedOS = OSType.Windows;
      } else if (osName.indexOf("nux") >= 0) {
        detectedOS = OSType.Linux;
      } else {
        detectedOS = OSType.Other;
      }
    }
    return detectedOS;
  }