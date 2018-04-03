package com.layoutxml.sabs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.layoutxml.sabs.db.DateConverter;

import java.util.Date;

@Entity(tableName = "BlockUrlProviders",
        indices = {@Index(value = {"url"}, unique = true)},
        foreignKeys = @ForeignKey(entity = PolicyPackage.class,
                parentColumns = "id",
                childColumns = "policyPackageId"))
@TypeConverters(DateConverter.class)
public class BlockUrlProvider {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "count")
    public int count;

    @ColumnInfo(name = "lastUpdated")
    public Date lastUpdated;

    @ColumnInfo(name = "deletable")
    public boolean deletable;

    @ColumnInfo(name = "selected")
    public boolean selected;

    @ColumnInfo(name = "policyPackageId")
    public String policyPackageId;
}
