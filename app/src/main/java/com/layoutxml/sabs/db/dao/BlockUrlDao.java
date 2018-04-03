package com.layoutxml.sabs.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.layoutxml.sabs.db.entity.BlockUrl;

import java.util.List;

@Dao
public interface BlockUrlDao {

    @Query("SELECT * FROM BlockUrl")
    List<BlockUrl> getAll();

    @Query("SELECT * FROM BlockUrl WHERE urlProviderId = :urlProviderId")
    List<BlockUrl> getUrlsByProviderId(long urlProviderId);

    @Query("DELETE FROM BlockUrl")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BlockUrl> blockUrls);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BlockUrl... blockUrls);
}
