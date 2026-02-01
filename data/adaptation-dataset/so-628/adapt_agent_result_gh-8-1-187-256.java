public synchronized SQLiteDatabase getReadableDatabase() {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                mDatabase = null;
            } else {
                // Return existing database even if it is read-only
                return mDatabase;
            }
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getReadableDatabase called recursively");
        }

        try {
            // First try to open the database in writable mode
            return getWritableDatabase();
        } catch (SQLiteException writeException) {
            if (mName == null) {
                // Temporary database cannot be opened read-only
                throw writeException;
            }

            Log.w(TAG, "Writable database open failed, falling back to read-only", writeException);

            SQLiteDatabase db = null;
            boolean success = false;
            try {
                mIsInitializing = true;
                String path = mDir + "/" + mName;
                db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);

                int version = db.getVersion();
                if (version != mNewVersion) {
                    throw new SQLiteException("Can't open read-only database with version "
                            + version + "; expected " + mNewVersion);
                }

                onOpen(db);
                Log.i(TAG, "Opened database in read-only mode");
                success = true;
                return db;
            } finally {
                mIsInitializing = false;
                if (success) {
                    if (mDatabase != null) {
                        try {
                            mDatabase.close();
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    mDatabase = db;
                } else if (db != null) {
                    db.close();
                }
            }
        }
    }