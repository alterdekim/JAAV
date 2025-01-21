package com.alterdekim.fridaapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ConfigDAO {
    @Query("SELECT * FROM config")
    Flowable<List<Config>> getAll();

    @Query("SELECT * FROM config WHERE uid IN (:cfgIds)")
    Flowable<List<Config>> loadAllByIds(int[] cfgIds);

    @Query("SELECT * FROM config WHERE uid = :cfgId")
    Single<Config> loadById(int cfgId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(Config... users);

    @Delete
    Completable delete(Config user);
}
