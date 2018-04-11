package com.layoutxml.sabs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.layoutxml.sabs.db.entity.BlockUrlProvider;

import java.util.List;

@Dao
public interface BlockUrlProviderDao {

    @Query("SELECT * FROM BlockUrlProviders")
    List<BlockUrlProvider> getAll2();

    @Query("SELECT * FROM BlockUrlProviders")
    LiveData<List<BlockUrlProvider>> getAll();

    @Query("SELECT * FROM BlockUrlProviders WHERE selected = :selected")
    List<BlockUrlProvider> getBlockUrlProviderBySelectedFlag(int selected);

    @Query("SELECT * FROM BlockUrlProviders WHERE selected = :selected AND deletable = 0")
    List<BlockUrlProvider> getStandardListsBySelectFlag(int selected);

    @Query("SELECT * FROM BlockUrlProviders WHERE url = :url")
    BlockUrlProvider getByUrl(String url);

    @Query("SELECT * FROM BlockUrlProviders WHERE deletable = 0")
    List<BlockUrlProvider> getStandardLists();

    @Query("SELECT * FROM BlockUrlProviders WHERE policyPackageId = 'default-policy'")
    List<BlockUrlProvider> getStandardListsNew();

    @Query("DELETE FROM blockurlproviders WHERE deletable = 0")
    void deleteStandardLists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] insertAll(BlockUrlProvider... urlProviders);

    @Update
    void updateBlockUrlProviders(BlockUrlProvider... blockUrlProviders);

    @Delete
    void delete(BlockUrlProvider blockUrlProvider);

}
