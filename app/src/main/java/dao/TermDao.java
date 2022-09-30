package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import entities.TermEntity;
import generics.GenericDao;

import java.util.List;

@Dao
public interface TermDao extends GenericDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TermEntity termEntity);

    @Update
    void update(TermEntity termEntity);

    @Delete
    void delete(TermEntity termEntity);

    @Query("SELECT * FROM terms ORDER BY id ASC")
    LiveData<List<TermEntity>> getAllTerms();
}
