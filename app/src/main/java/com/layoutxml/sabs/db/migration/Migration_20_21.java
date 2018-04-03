package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migration_20_21 extends Migration {

    public Migration_20_21(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE BlockUrlProviders ADD COLUMN policyPackageId TEXT DEFAULT 'default-policy' REFERENCES PolicyPackage(id)");
        database.execSQL("ALTER TABLE UserBlockUrl ADD COLUMN policyPackageId TEXT DEFAULT 'default-policy' REFERENCES PolicyPackage(id)");
        database.execSQL("ALTER TABLE WhiteUrl ADD COLUMN policyPackageId TEXT DEFAULT 'default-policy' REFERENCES PolicyPackage(id)");
    }
}
