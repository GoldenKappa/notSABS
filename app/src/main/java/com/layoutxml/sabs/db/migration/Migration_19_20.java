package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;

public class Migration_19_20 extends Migration {

    public Migration_19_20(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE FirewallWhitelistedPackage " +
                "(id INTEGER PRIMARY KEY, " +
                "packageName TEXT NOT NULL, " +
                "policyPackageId TEXT DEFAULT 'default-policy', " +
                "FOREIGN KEY (policyPackageId) REFERENCES PolicyPackage(id))");
        database.execSQL("CREATE UNIQUE INDEX firewall_whitelisted_package_policy_package_idx " +
                "ON FirewallWhitelistedPackage (packageName, policyPackageId)");
    }
}
