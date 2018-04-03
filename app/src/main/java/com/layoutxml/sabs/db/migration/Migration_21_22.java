package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migration_21_22 extends Migration {

    public Migration_21_22(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN numberOfDisabledPackages INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN numberOfHosts INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN numberOfUserBlockedDomains INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN numberOfUserWhitelistedDomains INTEGER DEFAULT 0");
        database.execSQL("ALTER TABLE PolicyPackage ADD COLUMN numberOfChangedPermissions INTEGER DEFAULT 0");
    }
}
