public static void executeAsAdministrator(String command, String args)
{
    win32.Shell32X.SHELLEXECUTEINFO execInfo = new win32.Shell32X.SHELLEXECUTEINFO();
    execInfo.lpFile = new WString(command);
    if (args != null)
    {
        execInfo.lpParameters = new WString(args);
    }
    // Use hidden show-state so the elevated process starts without displaying a window (SW_HIDE = 0)
    execInfo.nShow = 0;
    execInfo.fMask = win32.Shell32X.SEE_MASK_NOCLOSEPROCESS;
    execInfo.lpVerb = new WString("runas");
    boolean result = win32.Shell32X.INSTANCE.ShellExecuteEx(execInfo);

    if (!result)
    {
        int lastError = Kernel32.INSTANCE.GetLastError();
        String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
        throw new RuntimeException("Error performing elevation: " + lastError + ": " + errorMessage + " (apperror=" + execInfo.hInstApp + ")");
    }
}