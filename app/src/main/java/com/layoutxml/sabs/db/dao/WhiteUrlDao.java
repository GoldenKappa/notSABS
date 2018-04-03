package com.layoutxml.sabs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;

import com.layoutxml.sabs.db.DateConverter;
import com.layoutxml.sabs.db.entity.WhiteUrl;

import java.util.List;

@Dao
@TypeConverters(DateConverter.class)
public interface WhiteUrlDao {

    @Query("SELECT * FROM WhiteUrl")
    LiveData<List<WhiteUrl>> getAll();

    @Query("SELECT * FROM WhiteUrl")
    List<WhiteUrl> getAll2();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(WhiteUrl whiteUrl);

    @Query("DELETE FROM WhiteUrl WHERE url = :url")
    void deleteByUrl(String url);

    @Delete
    void delete(WhiteUrl whiteUrl);

}
