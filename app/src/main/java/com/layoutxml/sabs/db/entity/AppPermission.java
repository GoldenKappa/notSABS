package com.layoutxml.sabs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(
        tableName = "AppPermission",
        indices = {@Index(value = {"packageName", "permissionName", "policyPackageId"}, unique = true)},
        foreignKeys = @ForeignKey(entity = PolicyPackage.class,
                parentColumns = "id",
                childColumns = "policyPackageId")
)
public class AppPermission {

    @Ignore
    public static final int STATUS_DISALLOW = -1;

    @Ignore
    public static final int STATUS_NONE = 0;

    @Ignore
    public static final int STATUS_ALLOW = 1;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public long id;

    @ColumnInfo(name = "packageName")
    public String packageName;

    @ColumnInfo(name = "permissionName")
    public String permissionName;

    @ColumnInfo(name = "permissionStatus")
    public int permissionStatus;

    @ColumnInfo(name = "policyPackageId")
    public String policyPackageId;
}
