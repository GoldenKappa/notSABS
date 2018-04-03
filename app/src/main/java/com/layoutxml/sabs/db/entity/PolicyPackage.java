package com.layoutxml.sabs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.layoutxml.sabs.db.DateConverter;

import java.util.Date;
import java.util.List;

@Entity(
        tableName = "PolicyPackage"
)
@TypeConverters(DateConverter.class)
public class PolicyPackage {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    public String id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "active")
    public boolean active;

    @ColumnInfo(name = "createdAt")
    public Date createdAt;

    @ColumnInfo(name = "updatedAt")
    public Date updatedAt;

    @ColumnInfo(name = "numberOfDisabledPackages")
    public int numberOfDisabledPackages;

    @ColumnInfo(name = "numberOfHosts")
    public int numberOfHosts;

    @ColumnInfo(name = "numberOfUserBlockedDomains")
    public int numberOfUserBlockedDomains;

    @ColumnInfo(name = "numberOfUserWhitelistedDomains")
    public int numberOfUserWhitelistedDomains;

    @ColumnInfo(name = "numberOfChangedPermissions")
    public int numberOfChangedPermissions;

    @Ignore
    public List<DisabledPackage> disabledPackages;

    @Ignore
    public List<BlockUrlProvider> blockUrlProviders;

    @Ignore
    public List<UserBlockUrl> userBlockedDomains;

    @Ignore
    public List<WhiteUrl> userWhitelistedDomains;

    @Ignore
    public List<AppPermission> appPermissions;
    
}
