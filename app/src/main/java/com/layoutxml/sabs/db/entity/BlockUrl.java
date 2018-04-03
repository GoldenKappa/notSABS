package com.layoutxml.sabs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(
        tableName = "BlockUrl",
        foreignKeys = @ForeignKey(
                entity = BlockUrlProvider.class,
                parentColumns = "_id",
                childColumns = "urlProviderId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"url", "urlProviderId"}, unique = true)}
)
public class BlockUrl {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "url")
    public String url;

    @ColumnInfo(name = "urlProviderId")
    public long urlProviderId;

    public BlockUrl(String url, long urlProviderId) {
        this.url = url;
        this.urlProviderId = urlProviderId;
    }
}
