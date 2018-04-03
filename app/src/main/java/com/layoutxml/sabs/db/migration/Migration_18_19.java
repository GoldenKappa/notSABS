package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migration_18_19 extends Migration {

    public Migration_18_19(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN description TEXT");
    }
}
