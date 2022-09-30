package viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import database.C196Repository;
import entities.AssessmentEntity;

import java.util.List;

public class AssessmentViewModel extends AndroidViewModel {
    private C196Repository repo;

    public AssessmentViewModel(@NonNull Application application) {
        super(application);
        repo = new C196Repository(application);
    }

    public void insert(AssessmentEntity assessmentEntity) {
        repo.insert(assessmentEntity);
    }
    public void update(AssessmentEntity assessmentEntity) {
        repo.update(assessmentEntity);
    }
    public void delete(AssessmentEntity assessmentEntity) {
        repo.delete(assessmentEntity);
    }

    public LiveData<List<AssessmentEntity>> getCourseAssessments(int courseID) {
        return repo.getCourseAssessments(courseID);
    }

}
