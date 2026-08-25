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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(Config... configs);

    @Query("UPDATE config SET is_enabled = CASE WHEN uid = :configId THEN 1 ELSE 0 END")
    Completable enableSingle(int configId);

    @Query("UPDATE config SET is_enabled = 0")
    Completable disableAll();

    @Query("SELECT * FROM config WHERE is_enabled = 1")
    Single<Config> getEnabled();

    @Delete
    Completable delete(Config config);
}
