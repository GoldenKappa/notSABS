package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;


public class Migration_14_15 extends Migration {

    public Migration_14_15(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase supportSQLiteDatabase) {
        supportSQLiteDatabase.execSQL("ALTER TABLE AppInfo ADD COLUMN adhellWhitelisted INTEGER DEFAULT 0");
    }
}
