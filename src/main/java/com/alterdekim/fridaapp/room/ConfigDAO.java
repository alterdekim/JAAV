package com.alterdekim.fridaapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ConfigDAO {
    @Query("SELECT * FROM config")
    Observable<Config> getAll();

    @Query("SELECT * FROM config WHERE uid IN (:cfgIds)")
    Observable<Config> loadAllByIds(int[] cfgIds);

    @Query("SELECT * FROM config WHERE uid = :cfgId")
    Single<Config> loadById(int cfgId);

    @Insert
    Completable insertAll(Config... users);

    @Delete
    Completable delete(Config user);
}
