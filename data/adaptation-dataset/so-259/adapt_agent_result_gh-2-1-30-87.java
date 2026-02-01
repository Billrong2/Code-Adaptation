public static boolean setLogLevel(String loggerName, String logLevel) {
	final String logLevelUpper = (logLevel == null) ? "OFF" : logLevel.toUpperCase();
	try {
		// Use ROOT logger if given logger name is blank.
		if ((loggerName == null) || loggerName.trim().isEmpty()) {
			loggerName = (String) getFieldVaulue(LOGBACK_CLASSIC_LOGGER, "ROOT_LOGGER_NAME");
		}

		// Obtain logger by the name
		org.slf4j.Logger loggerObtained = LoggerFactory.getLogger(loggerName);
		if (loggerObtained == null) {
			logger.warn("No logger for the name: {}", loggerName);
			return false;
		}

		Object logLevelObj = getFieldVaulue(LOGBACK_CLASSIC_LEVEL, logLevelUpper);
		if (logLevelObj == null) {
			logger.warn("No such log level: {}", logLevelUpper);
			return false;
		}

		final Class<?>[] paramTypes = { logLevelObj.getClass() };
		final Object[] params = { logLevelObj };

		Class<?> clz = Class.forName(LOGBACK_CLASSIC_LOGGER);
		Method method = clz.getMethod("setLevel", paramTypes);
		method.invoke(loggerObtained, params);

		logger.debug("Log level set to {} for the logger '{}'", logLevelUpper, loggerName);
		return true;
	} catch (Exception e) {
		logger.warn("Couldn't set log level to {} for the logger '{}'", logLevelUpper, loggerName, e);
		return false;
	}
}