package com.layoutxml.sabs.db.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class Migration_16_17 extends Migration {

    public Migration_16_17(int startVersion, int endVersion) {
        super(startVersion, endVersion);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {

        database.execSQL("CREATE TABLE PolicyPackage " +
                "(id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL, " +
                "createdAt INTEGER, " +
                "updatedAt INTEGER) ");

        Date currentDate = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", "default-policy");
        contentValues.put("name", "Default Policy");
        contentValues.put("createdAt", currentDate.getTime());
        contentValues.put("updatedAt", currentDate.getTime());
        database.insert("PolicyPackage", SQLiteDatabase.CONFLICT_REPLACE, contentValues);

        database.execSQL("CREATE TABLE AppPermission " +
                "(id INTEGER PRIMARY KEY, " +
                "packageName TEXT NOT NULL, " +
                "permissionName TEXT NOT NULL, " +
                "permissionStatus INTEGER DEFAULT 0, " +
                "policyPackageId TEXT DEFAULT 'default-policy', " +
                "FOREIGN KEY (policyPackageId) REFERENCES PolicyPackage(id))");
        database.execSQL("CREATE UNIQUE INDEX app_permission_policy_package_idx " +
                "ON AppPermission (packageName, permissionName, policyPackageId)");

        database.execSQL("CREATE TABLE DisabledPackage " +
                "(id INTEGER PRIMARY KEY, " +
                "packageName TEXT NOT NULL, " +
                "policyPackageId TEXT DEFAULT 'default-policy', " +
                "FOREIGN KEY (policyPackageId) REFERENCES PolicyPackage(id))");
        database.execSQL("CREATE UNIQUE INDEX disabled_package_policy_package_idx " +
                "ON DisabledPackage (packageName, policyPackageId)");
    }
}
