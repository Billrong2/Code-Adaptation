public String getForegroundApp(android.content.Context context) {
    if (context == null) return null;
    try {
        // Pre-Lollipop: use ActivityManager.getRunningTasks
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (_activityManager == null) return null;
            try {
                java.util.List<android.app.ActivityManager.RunningTaskInfo> tasks = _activityManager.getRunningTasks(1);
                if (tasks != null && !tasks.isEmpty() && tasks.get(0).topActivity != null) {
                    return tasks.get(0).topActivity.getPackageName();
                }
            } catch (SecurityException ignored) {
                return null;
            }
            return null;
        }

        // Lollipop and above: procfs heuristic
        File procDir = new File("/proc");
        File[] pidDirs = procDir.listFiles();
        if (pidDirs == null) return null;

        String bestCmdline = null;
        int bestOomAdj = Integer.MAX_VALUE;
        int bestOomScore = Integer.MAX_VALUE;

        for (File pidDir : pidDirs) {
            if (pidDir == null || !pidDir.isDirectory()) continue;
            String pidName = pidDir.getName();
            int pid;
            try {
                pid = Integer.parseInt(pidName);
            } catch (NumberFormatException e) {
                continue;
            }

            try {
                // Read cgroup
                String cgroup = read(pidDir.getAbsolutePath() + "/cgroup");
                if (cgroup == null || cgroup.length() == 0) continue;
                if (cgroup.contains("bg_non_interactive")) continue; // background

                // Extract UID from cgroup
                int uid = -1;
                String[] lines = cgroup.split("\n");
                for (String line : lines) {
                    int idx = line.indexOf("uid_");
                    if (idx >= 0) {
                        int start = idx + 4;
                        int end = start;
                        while (end < line.length() && Character.isDigit(line.charAt(end))) end++;
                        try {
                            uid = Integer.parseInt(line.substring(start, end));
                        } catch (NumberFormatException ignored) {
                        }
                        break;
                    }
                }
                if (uid < 0) continue;

                // Derive appId and filter non-app/system processes
                int appId = uid % AID_USER;
                if (appId < AID_APP) continue;

                // Read cmdline (package name)
                String cmdline = read(pidDir.getAbsolutePath() + "/cmdline");
                if (cmdline == null || cmdline.trim().length() == 0) continue;
                cmdline = cmdline.replace('\u0000', ' ').trim();
                if (cmdline.startsWith("com.android.systemui")) continue;

                // Read oom scores
                int oomAdj = Integer.MAX_VALUE;
                int oomScore = Integer.MAX_VALUE;
                try {
                    String adjStr = read(pidDir.getAbsolutePath() + "/oom_score_adj");
                    if (adjStr != null) oomAdj = Integer.parseInt(adjStr.trim());
                } catch (Exception ignored) {
                }
                try {
                    String scoreStr = read(pidDir.getAbsolutePath() + "/oom_score");
                    if (scoreStr != null) oomScore = Integer.parseInt(scoreStr.trim());
                } catch (Exception ignored) {
                }

                // Prefer foreground (oom_score_adj == 0), then lowest oom_score
                boolean isBetter = false;
                if (oomAdj == 0 && bestOomAdj != 0) {
                    isBetter = true;
                } else if (oomAdj == bestOomAdj && oomScore < bestOomScore) {
                    isBetter = true;
                }

                if (isBetter) {
                    bestOomAdj = oomAdj;
                    bestOomScore = oomScore;
                    bestCmdline = cmdline;
                }
            } catch (IOException | SecurityException ignored) {
                // Skip unreadable or restricted entries
            }
        }
        return bestCmdline;
    } catch (Exception ignored) {
        return null;
    }
}