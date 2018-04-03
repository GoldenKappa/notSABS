package com.layoutxml.sabs.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.layoutxml.sabs.db.entity.AppPermission;

import java.util.List;

@Dao
public interface AppPermissionDao {

    @Query("SELECT * FROM AppPermission")
    List<AppPermission> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AppPermission appPermission);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AppPermission> appPermissions);

}
