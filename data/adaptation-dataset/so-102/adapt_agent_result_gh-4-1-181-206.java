private static boolean runCommand(String command, String args, String file) {
    if (command == null || command.trim().isEmpty()) {
      System.err.println("runCommand: command is null or empty");
      return false;
    }
    if (file == null || file.trim().isEmpty()) {
      System.err.println("runCommand: file is null or empty");
      return false;
    }
    String[] cmd = prepareCommand(command, args, file);
    if (cmd == null || cmd.length == 0) {
      System.err.println("runCommand: prepared command is empty");
      return false;
    }
    System.out.println("Executing command: " + java.util.Arrays.toString(cmd));
    try {
      Process process = Runtime.getRuntime().exec(cmd);
      if (process == null) {
        System.err.println("runCommand: exec returned null process");
        return false;
      }
      try {
        int exit = process.exitValue();
        // Process ended immediately
        if (exit == 0) {
          System.err.println("runCommand: process ended immediately with exit code 0");
        } else {
          System.err.println("runCommand: process crashed immediately with exit code " + exit);
        }
        // Clean up streams for an already-ended process
        try { process.getInputStream().close(); } catch (Exception ignore) {}
        try { process.getErrorStream().close(); } catch (Exception ignore) {}
        try { process.getOutputStream().close(); } catch (Exception ignore) {}
        return false;
      } catch (IllegalThreadStateException itse) {
        // Still running
        System.out.println("runCommand: process started successfully and is running");
        return true;
      }
    } catch (java.io.IOException ioe) {
      logErr("runCommand: IOException while executing command", ioe);
      return false;
    }
  }