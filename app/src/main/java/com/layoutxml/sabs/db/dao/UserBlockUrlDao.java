package com.layoutxml.sabs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.layoutxml.sabs.db.DateConverter;
import com.layoutxml.sabs.db.entity.UserBlockUrl;

import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface UserBlockUrlDao {
    @Query("SELECT * FROM UserBlockUrl")
    LiveData<List<UserBlockUrl>> getAll();

    @Query("SELECT * FROM UserBlockUrl")
    List<UserBlockUrl> getAll2();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(UserBlockUrl userBlockUrl);

    @Delete
    void delete(UserBlockUrl userBlockUrl);

    @Query("DELETE FROM UserBlockUrl WHERE url = :url")
    void deleteByUrl(String url);
}
