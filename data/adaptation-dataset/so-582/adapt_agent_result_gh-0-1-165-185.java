private static Logger createRequestLogger(String file) {
        final String LOGGER_NAME = "REQ_LOG";
        final String LOG_PATTERN = "%d{yyyy-MM-dd'T'HH:mm:ss.SSS} %msg%n";

        if (file == null || file.trim().isEmpty()) {
            throw new IllegalArgumentException("Request log file path must not be null or empty");
        }

        try {
            java.io.File logFile = new java.io.File(file);
            java.io.File parentDir = logFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = (Logger) LoggerFactory.getLogger(LOGGER_NAME);

            if (logger.getAppender(LOGGER_NAME) != null) {
                return logger;
            }

            PatternLayoutEncoder ple = new PatternLayoutEncoder();
            ple.setPattern(LOG_PATTERN);
            ple.setContext(lc);
            ple.start();

            FileAppender<ILoggingEvent> fileAppender = new FileAppender<>();
            fileAppender.setName(LOGGER_NAME);
            fileAppender.setFile(logFile.getAbsolutePath());
            fileAppender.setEncoder(ple);
            fileAppender.setContext(lc);
            fileAppender.start();

            logger.addAppender(fileAppender);
            logger.setLevel(Level.INFO);
            logger.setAdditive(false);

            return logger;
        } catch (RuntimeException e) {
            Logger fallback = (Logger) LoggerFactory.getLogger(Router.class);
            fallback.error("Failed to initialize request logger", e);
            throw e;
        }
    }