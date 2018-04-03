package com.layoutxml.sabs.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.layoutxml.sabs.db.dao.AppInfoDao;
import com.layoutxml.sabs.db.dao.AppPermissionDao;
import com.layoutxml.sabs.db.dao.BlockUrlDao;
import com.layoutxml.sabs.db.dao.BlockUrlProviderDao;
import com.layoutxml.sabs.db.dao.DisabledPackageDao;
import com.layoutxml.sabs.db.dao.FirewallWhitelistedPackageDao;
import com.layoutxml.sabs.db.dao.PolicyPackageDao;
import com.layoutxml.sabs.db.dao.ReportBlockedUrlDao;
import com.layoutxml.sabs.db.dao.UserBlockUrlDao;
import com.layoutxml.sabs.db.dao.WhiteUrlDao;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.db.entity.AppPermission;
import com.layoutxml.sabs.db.entity.BlockUrl;
import com.layoutxml.sabs.db.entity.BlockUrlProvider;
import com.layoutxml.sabs.db.entity.DisabledPackage;
import com.layoutxml.sabs.db.entity.FirewallWhitelistedPackage;
import com.layoutxml.sabs.db.entity.PolicyPackage;
import com.layoutxml.sabs.db.entity.ReportBlockedUrl;
import com.layoutxml.sabs.db.entity.UserBlockUrl;
import com.layoutxml.sabs.db.entity.WhiteUrl;
import com.layoutxml.sabs.db.migration.Migration_14_15;
import com.layoutxml.sabs.db.migration.Migration_15_16;
import com.layoutxml.sabs.db.migration.Migration_16_17;
import com.layoutxml.sabs.db.migration.Migration_17_18;
import com.layoutxml.sabs.db.migration.Migration_18_19;
import com.layoutxml.sabs.db.migration.Migration_19_20;
import com.layoutxml.sabs.db.migration.Migration_20_21;
import com.layoutxml.sabs.db.migration.Migration_21_22;

@Database(entities = {
        AppInfo.class,
        AppPermission.class,
        BlockUrl.class,
        BlockUrlProvider.class,
        DisabledPackage.class,
        FirewallWhitelistedPackage.class,
        PolicyPackage.class,
        ReportBlockedUrl.class,
        UserBlockUrl.class,
        WhiteUrl.class
}, version = 22)
public abstract class AppDatabase extends RoomDatabase {
    private static final Migration MIGRATION_14_15 = new Migration_14_15(14, 15);
    private static final Migration MIGRATION_15_16 = new Migration_15_16(15, 16);
    private static final Migration MIGRATION_16_17 = new Migration_16_17(16, 17);
    private static final Migration MIGRATION_17_18 = new Migration_17_18(17, 18);
    private static final Migration MIGRATION_18_19 = new Migration_18_19(18, 19);
    private static final Migration MIGRATION_19_20 = new Migration_19_20(19, 20);
    private static final Migration MIGRATION_20_21 = new Migration_20_21(20, 21);
    private static final Migration MIGRATION_21_22 = new Migration_21_22(21, 22);
    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "adhell-database")
                            .addMigrations(MIGRATION_14_15)
                            .addMigrations(MIGRATION_15_16)
                            .addMigrations(MIGRATION_16_17)
                            .addMigrations(MIGRATION_17_18)
                            .addMigrations(MIGRATION_18_19)
                            .addMigrations(MIGRATION_19_20)
                            .addMigrations(MIGRATION_20_21)
                            .addMigrations(MIGRATION_21_22)
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    public abstract BlockUrlDao blockUrlDao();

    public abstract BlockUrlProviderDao blockUrlProviderDao();

    public abstract ReportBlockedUrlDao reportBlockedUrlDao();

    public abstract AppInfoDao applicationInfoDao();

    public abstract WhiteUrlDao whiteUrlDao();

    public abstract UserBlockUrlDao userBlockUrlDao();

    public abstract PolicyPackageDao policyPackageDao();

    public abstract DisabledPackageDao disabledPackageDao();

    public abstract FirewallWhitelistedPackageDao firewallWhitelistedPackageDao();

    public abstract AppPermissionDao appPermissionDao();

}