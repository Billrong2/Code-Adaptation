private void runScript(final Connection connection, final Reader reader) throws IOException, SQLException {
    final String COMMENT_PREFIX = "--";
    LineNumberReader lineReader = null;
    Statement stmt = null;
    ResultSet rs = null;
    boolean hadError = false;
    try {
        if (reader == null) {
            return;
        }
        lineReader = (reader instanceof LineNumberReader)
                ? (LineNumberReader) reader
                : new LineNumberReader(reader);
        final String delimiter = getDelimiter();
        final StringBuilder command = new StringBuilder();
        String line;
        while ((line = lineReader.readLine()) != null) {
            final String trimmed = line.trim();
            if (trimmed.length() == 0 || trimmed.startsWith(COMMENT_PREFIX)) {
                continue;
            }
            boolean endOfCommand = false;
            String workLine = line;
            if (fullLineDelimiter) {
                if (trimmed.equals(delimiter)) {
                    endOfCommand = true;
                }
            } else {
                if (trimmed.endsWith(delimiter)) {
                    endOfCommand = true;
                    workLine = line.substring(0, line.lastIndexOf(delimiter));
                }
            }
            if (!endOfCommand) {
                command.append(workLine).append('\n');
                continue;
            }
            command.append(workLine);
            final String sql = command.toString().trim();
            command.setLength(0);
            if (sql.length() == 0) {
                continue;
            }
            try {
                println(sql);
                stmt = connection.createStatement();
                final boolean hasResultSet = stmt.execute(sql);
                if (hasResultSet) {
                    rs = stmt.getResultSet();
                    if (rs != null) {
                        final ResultSetMetaData md = rs.getMetaData();
                        final int cols = md.getColumnCount();
                        for (int i = 1; i <= cols; i++) {
                            print(md.getColumnLabel(i));
                            if (i < cols) {
                                print("\t");
                            }
                        }
                        println("");
                        while (rs.next()) {
                            for (int i = 1; i <= cols; i++) {
                                print(rs.getString(i));
                                if (i < cols) {
                                    print("\t");
                                }
                            }
                            println("");
                        }
                    }
                }
                if (autoCommit && !connection.getAutoCommit()) {
                    connection.commit();
                }
            } catch (SQLException e) {
                hadError = true;
                printlnError("Error executing: " + sql);
                if (stopOnError) {
                    throw e;
                }
            } finally {
                if (rs != null) {
                    try { rs.close(); } catch (Exception ignore) { }
                    rs = null;
                }
                if (stmt != null) {
                    try { stmt.close(); } catch (Exception ignore) { }
                    stmt = null;
                }
                Thread.yield();
            }
        }
        if (!autoCommit) {
            connection.commit();
        }
    } finally {
        if (!autoCommit) {
            try { connection.rollback(); } catch (Exception ignore) { }
        }
        flush();
    }
}