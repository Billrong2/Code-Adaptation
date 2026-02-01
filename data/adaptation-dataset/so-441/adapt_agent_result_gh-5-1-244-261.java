private static final EnumOS getOs() {
    String osName = System.getProperty("os.name");
    if (osName == null) {
      return EnumOS.unknown;
    }
    String s = osName.toLowerCase();

    if (s.contains("win")) {
      return EnumOS.windows;
    } else if (s.contains("mac")) {
      return EnumOS.macos;
    } else if (s.contains("solaris") || s.contains("sunos")) {
      return EnumOS.solaris;
    } else if (s.contains("linux") || s.contains("unix")) {
      return EnumOS.linux;
    } else {
      return EnumOS.unknown;
    }
  }