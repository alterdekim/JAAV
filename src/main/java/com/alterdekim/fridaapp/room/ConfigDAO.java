package com.alterdekim.fridaapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
import java.util.Optional;

@Dao
public interface ConfigDAO {
    @Query("SELECT * FROM config")
    List<Config> getAll();

    @Query("SELECT * FROM config WHERE uid IN (:cfgIds)")
    List<Config> loadAllByIds(int[] cfgIds);

    @Query("SELECT * FROM config WHERE uid = :cfgId")
    Optional<Config> loadById(int cfgId);

    @Insert
    void insertAll(Config... users);

    @Delete
    void delete(Config user);
}
