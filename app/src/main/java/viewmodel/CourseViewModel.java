package viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import database.C196Repository;
import entities.CourseEntity;
import entities.TermEntity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class CourseViewModel extends AndroidViewModel {
    private C196Repository repo;

    public CourseViewModel(@NonNull Application application) {
        super(application);
        repo = new C196Repository(application);
    }

    public void insert(CourseEntity courseEntity) {
        repo.insert(courseEntity);
    }
    public void update(CourseEntity courseEntity) {
        repo.update(courseEntity);
    }
    public void delete(CourseEntity courseEntity) {
        repo.delete(courseEntity);
    }

    public LiveData<List<CourseEntity>> getLiveTermCourses(int termID) {
        return repo.getLiveTermCourses(termID);
    }
    public List<CourseEntity> getTermCourses(int termID) throws ExecutionException, InterruptedException {
        return repo.getTermCourses(termID);
    }
}
