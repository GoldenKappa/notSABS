package com.layoutxml.sabs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.layoutxml.sabs.db.DateConverter;


@Entity(
        tableName = "AppInfo",
        indices = {@Index("appName"), @Index("installTime"), @Index("disabled")}
)
@TypeConverters(DateConverter.class)
public class AppInfo {
    @PrimaryKey
    public long id;

    @ColumnInfo(name = "packageName")
    public String packageName;

    @ColumnInfo(name = "appName")
    public String appName;

    @ColumnInfo(name = "installTime")
    public long installTime;

    @ColumnInfo(name = "system")
    public boolean system;

    @ColumnInfo(name = "adhellWhitelisted")
    public boolean adhellWhitelisted;

    @ColumnInfo(name = "disabled")
    public boolean disabled;
}
