private float readUsage(android.content.Context context)
{
    final int IDX_IDLE = 5; // shifted by +1 due to single-space split
    final int IDX_CPU_USER = 3;
    final int IDX_CPU_NICE = 4;
    final int IDX_CPU_SYSTEM = 6;
    final int IDX_CPU_IDLE = 7; // not used directly
    final int IDX_CPU_IOWAIT = 8;
    final int IDX_CPU_IRQ = 9;

    java.io.RandomAccessFile reader = null;

    try
    {
        reader = new java.io.RandomAccessFile("/proc/stat", "r");
        String load = reader.readLine();

        if (load == null)
            return 0f;

        String[] toks = load.split(" "); // split on single space, may include empty tokens

        if (toks.length <= IDX_CPU_IRQ)
            return 0f;

        long idle1 = Long.parseLong(toks[IDX_IDLE]);
        long cpu1 = Long.parseLong(toks[IDX_CPU_USER])
                + Long.parseLong(toks[IDX_CPU_NICE])
                + Long.parseLong(toks[IDX_CPU_SYSTEM])
                + Long.parseLong(toks[IDX_CPU_IOWAIT])
                + Long.parseLong(toks[IDX_CPU_IRQ]);

        try
        {
            Thread.sleep(360);
        }
        catch (InterruptedException ie)
        {
            edu.northwestern.cbits.purple_robot_manager.logging.LogManager.getInstance(context).logException(ie);
            Thread.currentThread().interrupt();
            return 0f;
        }

        reader.seek(0);
        load = reader.readLine();

        if (load == null)
            return 0f;

        toks = load.split(" ");

        if (toks.length <= IDX_CPU_IRQ)
            return 0f;

        long idle2 = Long.parseLong(toks[IDX_IDLE]);
        long cpu2 = Long.parseLong(toks[IDX_CPU_USER])
                + Long.parseLong(toks[IDX_CPU_NICE])
                + Long.parseLong(toks[IDX_CPU_SYSTEM])
                + Long.parseLong(toks[IDX_CPU_IOWAIT])
                + Long.parseLong(toks[IDX_CPU_IRQ]);

        long totalDiff = (cpu2 + idle2) - (cpu1 + idle1);
        long cpuDiff = cpu2 - cpu1;

        if (totalDiff <= 0)
            return 0f;

        return (float) cpuDiff / (float) totalDiff;
    }
    catch (java.io.IOException ex)
    {
        edu.northwestern.cbits.purple_robot_manager.logging.LogManager.getInstance(context).logException(ex);
    }
    catch (NumberFormatException ex)
    {
        edu.northwestern.cbits.purple_robot_manager.logging.LogManager.getInstance(context).logException(ex);
    }
    finally
    {
        if (reader != null)
        {
            try
            {
                reader.close();
            }
            catch (java.io.IOException ex)
            {
                edu.northwestern.cbits.purple_robot_manager.logging.LogManager.getInstance(context).logException(ex);
            }
        }
    }

    return 0f;
}