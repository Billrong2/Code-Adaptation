private static boolean openSystemSpecific(String target) {
    if (target == null || target.trim().isEmpty()) {
      return false;
    }
    EnumOS os = getOs();
    try {
      if (os != null && os.isLinux()) {
        // Try common Linux open commands in order
        if (runCommand("kde-open", "%s", target)) {
          return true;
        }
        if (runCommand("gnome-open", "%s", target)) {
          return true;
        }
        if (runCommand("xdg-open", "%s", target)) {
          return true;
        }
        return false;
      } else if (os != null && os.isMac()) {
        // macOS
        return runCommand("open", "%s", target);
      } else if (os != null && os.isWindows()) {
        // Windows
        return runCommand("explorer", "%s", target);
      } else {
        return false;
      }
    } catch (SecurityException se) {
      logErr("Security exception while trying to open target.", se);
      return false;
    } catch (Exception e) {
      logErr("Unexpected error while trying to open target.", e);
      return false;
    }
  }