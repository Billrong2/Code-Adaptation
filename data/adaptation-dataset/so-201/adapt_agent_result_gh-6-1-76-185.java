public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null) {
            if (!mDatabase.isOpen()) {
                // darn! the user closed the database by calling
                // mDatabase.close()
                mDatabase = null;
            } else if (!mDatabase.isReadOnly()) {
                return mDatabase; // The database is already open for business
            }
        }

        if (mIsInitializing) {
            throw new IllegalStateException(
                    "getWritableDatabase called recursively");
        }

        boolean success = false;
        SQLiteDatabase db = null;
        try {
            mIsInitializing = true;
            if (mName == null) {
                db = SQLiteDatabase.create(null);
            } else {
                final String path = mDir + "/" + mName;
                db = SQLiteDatabase.openDatabase(path, null,
                        SQLiteDatabase.CREATE_IF_NECESSARY);
            }

            int version = db.getVersion();
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else if (version > mNewVersion) {
                        onDowngrade(db, version, mNewVersion);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);
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
            } else {
                if (db != null) {
                    db.close();
                }
            }
        }
    }