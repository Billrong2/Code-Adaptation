private CompiledScript tryCompiling(final String command, final int lineCount, final int lastLineLength)
		throws ScriptException
	{
		CompiledScript result = null;
		try {
			final Compilable compilable = (Compilable) engine;
			result = compilable.compile(command);
		}
		catch (final ScriptException se) {
			boolean rethrow = true;
			if (se.getCause() != null) {
				final Integer col = columnNumber(se);
				final Integer line = lineNumber(se);
				// Swallow the exception if it occurs at the last character of the input.
				if (isLastCharacter(col, line, lineCount, lastLineLength)) {
					rethrow = false;
				}
				else if (log != null && log.isDebug()) {
					final String msg = se.getCause().getMessage();
					log.debug("L" + line + " C" + col + "(" + lineCount + "," + lastLineLength + "): " + msg);
					log.debug("in '" + command + "'");
				}
			}

			if (rethrow) {
				reset();
				throw se;
			}
		}

		expectingMoreInput = result == null;
		return result;
	}