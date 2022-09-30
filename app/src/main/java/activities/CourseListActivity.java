package activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mbro.wguapp.R;
import adapters.CourseAdapter;
import entities.CourseEntity;
import viewmodel.CourseViewModel;

public class CourseListActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_TERM_ID = "activities.TERM_ID";
    public static final String EXTRA_COURSE_TERM_TITLE = "activities.TERM_TITLE";

    private int termID;

    private CourseViewModel courseViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);

        FloatingActionButton buttonAddCourse = findViewById(R.id.btn_add_course);
        buttonAddCourse.setOnClickListener(v -> {
            Intent addCourseIntent = new Intent(CourseListActivity.this, AddEditCourseActivity.class);
            startActivityForResult(addCourseIntent, AddEditCourseActivity.REQUEST_ADD_COURSE);
        });

        Intent loadCourseListIntent = getIntent();
        termID = loadCourseListIntent.getIntExtra(EXTRA_COURSE_TERM_ID, -1);
        String termTitle = loadCourseListIntent.getStringExtra(EXTRA_COURSE_TERM_TITLE);
        setTitle(termTitle + " Courses");

        RecyclerView recyclerView = findViewById(R.id.courseListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final CourseAdapter adapter = new CourseAdapter();
        recyclerView.setAdapter(adapter);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        courseViewModel.getLiveTermCourses(termID).observe(this, adapter::setCourses);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                CourseEntity deletedCourse = adapter.getCourseAt(viewHolder.getAdapterPosition());
                courseViewModel.delete(deletedCourse);
                Toast.makeText(CourseListActivity.this, "Course deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(courseEntity -> {
            Intent intent = new Intent(CourseListActivity.this, CourseActivity.class);
            intent.putExtra(CourseActivity.EXTRA_COURSE_TERM_ID, termID);
            intent.putExtra(CourseActivity.EXTRA_COURSE_ID, courseEntity.getId());
            intent.putExtra(CourseActivity.EXTRA_COURSE_TITLE, courseEntity.getTitle());
            intent.putExtra(CourseActivity.EXTRA_COURSE_START_DATE, courseEntity.getStartDate());
            intent.putExtra(CourseActivity.EXTRA_COURSE_END_DATE, courseEntity.getEndDate());
            intent.putExtra(CourseActivity.EXTRA_COURSE_ALERT, courseEntity.isAlert());
            intent.putExtra(CourseActivity.EXTRA_COURSE_STATUS, courseEntity.getStatus());
            intent.putExtra(CourseActivity.EXTRA_COURSE_MENTOR_NAME, courseEntity.getMentorName());
            intent.putExtra(CourseActivity.EXTRA_COURSE_MENTOR_EMAIL, courseEntity.getMentorEmail());
            intent.putExtra(CourseActivity.EXTRA_COURSE_MENTOR_PHONE, courseEntity.getMentorPhone());
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AddEditCourseActivity.REQUEST_ADD_COURSE && resultCode == RESULT_OK) {
            int termID = getIntent().getIntExtra(EXTRA_COURSE_TERM_ID, -1);
            assert data != null;
            String title = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_TITLE);
            String startdate = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_START_DATE);
            String endDate = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_END_DATE);
            boolean courseAlert = data.getBooleanExtra(AddEditCourseActivity.EXTRA_COURSE_ALERT, false);
            int status = data.getIntExtra(AddEditCourseActivity.EXTRA_COURSE_STATUS, -1);
            String mentorName = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_NAME);
            String mentorPhone = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_PHONE);
            String mentorEmail = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_EMAIL);

            if(termID == -1) throw new AssertionError("termID cannot be -1");
            CourseEntity courseEntity = new CourseEntity(termID, title, startdate, endDate, courseAlert,
                    status, mentorName, mentorPhone, mentorEmail);
            courseViewModel.insert(courseEntity);

            Toast.makeText(this, title + " added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Course not added", Toast.LENGTH_SHORT).show();
        }
    }
}
