package activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.SimpleTimeZone;

import adapters.CourseAdapter;
import alarms.CourseAlarmReceiver;
import entities.CourseEntity;
import viewmodel.CourseViewModel;

public class CourseListActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_TERM_ID = "activities.TERM_ID";
    public static final String EXTRA_COURSE_TERM_TITLE = "activities.TERM_TITLE";

    public static final int ALARM_COURSE_START = 50;
    public static final int ALARM_COURSE_END = 100;

    private int termID;
    private AlarmManager courseAlarmManager;
    PendingIntent startCoursePendingIntent;
    PendingIntent endCoursePendingIntent;

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
            intent.putExtra(CourseActivity.EXTRA_START_COURSE_ALERT, courseEntity.isStartDateAlert());
            intent.putExtra(CourseActivity.EXTRA_END_COURSE_ALERT,courseEntity.isEndDateAlert());
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
            boolean startCourseAlert = data.getBooleanExtra(AddEditCourseActivity.EXTRA_START_COURSE_ALERT, false);
            boolean endCourseAlert = data.getBooleanExtra(AddEditCourseActivity.EXTRA_END_COURSE_ALERT, false);

            int status = data.getIntExtra(AddEditCourseActivity.EXTRA_COURSE_STATUS, -1);
            String mentorName = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_NAME);
            String mentorPhone = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_PHONE);
            String mentorEmail = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_EMAIL);

            if(termID == -1) throw new AssertionError("termID cannot be -1");

           if (startCourseAlert || endCourseAlert){
               courseAlarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);

               if (startCourseAlert){
                   Intent startCourseAlarmIntent = new Intent(this, CourseAlarmReceiver.class);

                   startCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TITLE, title + " Alert!");
                   startCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TEXT, title + " will be starting soon (" + startdate + ")!");

                   Calendar startCourseAlarmCalendar = Calendar.getInstance();
                   long startAlarmTime = startCourseAlarmCalendar.getTimeInMillis();

                   SimpleDateFormat dateFormat = new SimpleDateFormat(AddEditCourseActivity.DATE_FORMAT, Locale.ENGLISH);

                   try {
                       startCourseAlarmCalendar.setTime(Objects.requireNonNull(dateFormat.parse(startdate)));
                       startCourseAlarmCalendar.set(Calendar.HOUR, 8);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   startCoursePendingIntent = PendingIntent.getBroadcast(this, ALARM_COURSE_START, startCourseAlarmIntent, PendingIntent.FLAG_IMMUTABLE);
                   courseAlarmManager.set(AlarmManager.RTC_WAKEUP, startAlarmTime, startCoursePendingIntent);
               } else {
                   if (courseAlarmManager != null){
                       courseAlarmManager.cancel(startCoursePendingIntent);
                       startCoursePendingIntent.cancel();
                   }
               }
               if (endCourseAlert){
                   Intent endCourseAlarmIntent = new Intent(this, CourseAlarmReceiver.class);

                   endCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TITLE, title + " Alert!");
                   endCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TEXT, title + " will be ending soon (" + endDate + ")!");

                   Calendar endCourseAlarmCalendar = Calendar.getInstance();
                   long endAlarmTime = endCourseAlarmCalendar.getTimeInMillis();

                   SimpleDateFormat dateFormat = new SimpleDateFormat(AddEditCourseActivity.DATE_FORMAT, Locale.ENGLISH);

                   try {
                       endCourseAlarmCalendar.setTime(Objects.requireNonNull(dateFormat.parse(endDate)));
                       endCourseAlarmCalendar.set(Calendar.HOUR, 8);
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }

                   endCoursePendingIntent = PendingIntent.getBroadcast(this, ALARM_COURSE_START, endCourseAlarmIntent, PendingIntent.FLAG_IMMUTABLE);
                   courseAlarmManager.set(AlarmManager.RTC_WAKEUP, endAlarmTime, endCoursePendingIntent);
               } else {
                   if (courseAlarmManager != null){
                       courseAlarmManager.cancel(endCoursePendingIntent);
                       endCoursePendingIntent.cancel();
                   }
               }

           }

            CourseEntity courseEntity = new CourseEntity(termID, title, startdate, endDate, startCourseAlert, endCourseAlert,
                    status, mentorName, mentorPhone, mentorEmail);
            courseViewModel.insert(courseEntity);

            Toast.makeText(this, title + " added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Course not added", Toast.LENGTH_SHORT).show();
        }

    }
}
