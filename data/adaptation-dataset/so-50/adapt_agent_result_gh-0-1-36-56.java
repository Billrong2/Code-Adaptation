/**
   * Detect the operating system from the os.name System property and cache
   * the result.
   *
   * @return the operating system detected
   */
  public static OSType getOperatingSystemType() {
    if (detectedOS == null) {
      String osName = System.getProperty("os.name", "generic")
          .toLowerCase(Locale.ENGLISH);

      if (osName.indexOf("mac") >= 0 || osName.indexOf("darwin") >= 0) {
        detectedOS = OSType.MacOS;
      } else if (osName.indexOf("win") >= 0) {
        detectedOS = OSType.Windows;
      } else if (osName.indexOf("linux") >= 0) {
        detectedOS = OSType.Linux;
      } else {
        detectedOS = OSType.Other;
      }
    }

    return detectedOS;
  }