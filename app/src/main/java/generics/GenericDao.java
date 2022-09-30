package generics;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import entities.AssessmentEntity;
import entities.CourseEntity;
import entities.NoteEntity;
import entities.TermEntity;

public interface GenericDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TermEntity entity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CourseEntity entity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NoteEntity entity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AssessmentEntity entity);

    @Update
    void update(TermEntity entity);
    @Update
    void update(CourseEntity entity);
    @Update
    void update(NoteEntity entity);
    @Update
    void update(AssessmentEntity entity);

    @Delete
    void delete(TermEntity entity);
    @Delete
    void delete(CourseEntity entity);
    @Delete
    void delete(NoteEntity entity);
    @Delete
    void delete(AssessmentEntity entity);
}
