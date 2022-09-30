package activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mbro.wguapp.R;
import alarms.CourseAlarmReceiver;
import entities.CourseEntity;
import viewmodel.CourseViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;


public class CourseActivity extends AppCompatActivity {
    public static final String EXTRA_COURSE_TERM_ID =
            "com.mbro.wguapp.activities.COURSE_TERM_ID";
    public static final String EXTRA_COURSE_ID =
            "com.mbro.wguapp.activities.COURSE_ID";
    public static final String EXTRA_COURSE_TITLE =
            "com.mbro.wguapp.activities.COURSE_TITLE";
    public static final String EXTRA_COURSE_START_DATE =
            "com.mbro.wguapp.activities.COURSE_START_DATE";
    public static final String EXTRA_COURSE_END_DATE =
            "com.mbro.wguapp.activities.COURSE_END_DATE";
    public static final String EXTRA_COURSE_ALERT =
            "com.mbro.wguapp.activities.COURSE_ALERT";
    public static final String EXTRA_COURSE_STATUS =
            "com.mbro.wguapp.activities.COURSE_STATUS";
    public static final String EXTRA_COURSE_MENTOR_NAME =
            "com.mbro.wguapp.activities.COURSE_MENTOR_NAME";
    public static final String EXTRA_COURSE_MENTOR_PHONE =
            "com.mbro.wguapp.activities.COURSE_MENTOR_PHONE";
    public static final String EXTRA_COURSE_MENTOR_EMAIL =
            "com.mbro.wguapp.activities.COURSE_MENTOR_EMAIL";

    public static final int ALARM_COURSE_START = 50;
    public static final int ALARM_COURSE_END = 100;


    public static final int STATUS_DROPPED = 0;
    public static final int STATUS_PLANNED = 1;
    public static final int STATUS_IN_PROGRESS = 2;
    public static final int STATUS_COMPLETED = 3;

    public static final int EDIT_COURSE_REQUEST = 5;

    private AlarmManager courseAlarmManager;
    PendingIntent startCoursePendingIntent;
    PendingIntent endCoursePendingIntent;

    private CourseViewModel courseViewModel;

    private int termID;
    private int courseID;
    private String courseTitle;
    private TextView textViewTitle;
    private TextView textViewStartDate;
    private TextView textViewEndDate;
    private int status;
    private TextView textViewCourseStatus;
    private TextView textViewCourseMentorName;
    private TextView textViewCourseMentorPhone;
    private TextView textViewCourseMentorEmail;
    private boolean alarmEnabled;
    private ImageView imageViewAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        setContentView(R.layout.activity_course);

        textViewTitle = findViewById(R.id.detailed_course_title);
        textViewStartDate = findViewById(R.id.detailed_course_start_date);
        textViewEndDate = findViewById(R.id.detailed_course_end_date);
        textViewCourseStatus = findViewById(R.id.detailed_course_status);
        imageViewAlarm = findViewById(R.id.detailed_image_alarm);
        textViewCourseMentorName = findViewById(R.id.detailed_course_mentor_name);
        textViewCourseMentorPhone = findViewById(R.id.detailed_course_mentor_phone_number);
        textViewCourseMentorEmail = findViewById(R.id.detailed_course_mentor_email_address);

        Intent parentIntent = getIntent();
        termID = parentIntent.getIntExtra(EXTRA_COURSE_TERM_ID, -1);
        courseID = parentIntent.getIntExtra(EXTRA_COURSE_ID, -1);

        setTitle(parentIntent.getStringExtra(EXTRA_COURSE_TITLE));

        this.courseTitle = parentIntent.getStringExtra(EXTRA_COURSE_TITLE);
        textViewTitle.setText(courseTitle);
        textViewStartDate.setText(parentIntent.getStringExtra(EXTRA_COURSE_START_DATE));
        textViewEndDate.setText(parentIntent.getStringExtra(EXTRA_COURSE_END_DATE));
        status = parentIntent.getIntExtra(EXTRA_COURSE_STATUS, -1);
        textViewCourseStatus.setText(CourseActivity.getStatus(status));
        textViewCourseMentorName.setText(parentIntent.getStringExtra(EXTRA_COURSE_MENTOR_NAME));
        textViewCourseMentorPhone.setText(parentIntent.getStringExtra(EXTRA_COURSE_MENTOR_PHONE));
        textViewCourseMentorEmail.setText(parentIntent.getStringExtra(EXTRA_COURSE_MENTOR_EMAIL));
        alarmEnabled = parentIntent.getBooleanExtra(EXTRA_COURSE_ALERT, false);
        if (alarmEnabled) {
            imageViewAlarm.setVisibility(View.VISIBLE);
        } else {
            imageViewAlarm.setVisibility(View.INVISIBLE);
        }

        FloatingActionButton buttonEditCourse = findViewById(R.id.btn_edit_course);
        buttonEditCourse.setOnClickListener(v -> {
            Intent editCourseIntent = new Intent(CourseActivity.this, AddEditCourseActivity.class);
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_ID, courseID);
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_TITLE, textViewTitle.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_START_DATE, textViewStartDate.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_END_DATE, textViewEndDate.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_STATUS, status);
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_NAME, textViewCourseMentorName.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_PHONE, textViewCourseMentorPhone.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_EMAIL, textViewCourseMentorEmail.getText().toString());
            editCourseIntent.putExtra(AddEditCourseActivity.EXTRA_COURSE_ALERT, alarmEnabled);
            startActivityForResult(editCourseIntent, EDIT_COURSE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_COURSE_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            int courseID = data.getIntExtra(AddEditCourseActivity.EXTRA_COURSE_ID, -1);
            String courseTitle = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_TITLE);
            String courseStartDate = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_START_DATE);
            String courseEndDate = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_END_DATE);
            boolean alarmEnabled = data.getBooleanExtra(AddEditCourseActivity.EXTRA_COURSE_ALERT, false);
            int courseStatus = data.getIntExtra(AddEditCourseActivity.EXTRA_COURSE_STATUS, -1);
            String courseMentorName = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_NAME);
            String courseMentorPhone = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_PHONE);
            String courseMentorEmail = data.getStringExtra(AddEditCourseActivity.EXTRA_COURSE_MENTOR_EMAIL);

            if (courseID == -1) {
                Toast.makeText(this, "Error, course not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            if (alarmEnabled) {
                imageViewAlarm.setVisibility(View.VISIBLE);
            } else {
                imageViewAlarm.setVisibility(View.INVISIBLE);
            }

            textViewTitle.setText(courseTitle);
            textViewStartDate.setText(courseStartDate);
            textViewEndDate.setText(courseEndDate);
            textViewCourseStatus.setText(getStatus(courseStatus));
            textViewCourseMentorName.setText(courseMentorName);
            textViewCourseMentorPhone.setText(courseMentorPhone);
            textViewCourseMentorEmail.setText(courseMentorEmail);

            if (alarmEnabled) {
                courseAlarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
                Intent startCourseAlarmIntent = new Intent(this, CourseAlarmReceiver.class);
                startCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TITLE, courseTitle + " Alert!");
                startCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TEXT, courseTitle + " will be starting soon (" + courseStartDate + ")");
                Intent endCourseAlarmIntent = new Intent(this, CourseAlarmReceiver.class);
                endCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TITLE, courseTitle + " Alert!");
                endCourseAlarmIntent.putExtra(CourseAlarmReceiver.ALARM_NOTIFICATION_TEXT, courseTitle + " will be ending soon (" + courseEndDate + ")");

                Calendar startCourseAlarmCalendar = Calendar.getInstance();
                Calendar endCourseAlarmCalendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat(AddEditCourseActivity.DATE_FORMAT, Locale.ENGLISH);
                try {
                    startCourseAlarmCalendar.setTime(Objects.requireNonNull(dateFormat.parse(courseStartDate)));
                    startCourseAlarmCalendar.set(Calendar.HOUR, 8);
                    endCourseAlarmCalendar.setTime(Objects.requireNonNull(dateFormat.parse(courseEndDate)));
                    endCourseAlarmCalendar.set(Calendar.HOUR, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                startCoursePendingIntent = PendingIntent.getBroadcast(this, ALARM_COURSE_START, startCourseAlarmIntent, 0);
                endCoursePendingIntent = PendingIntent.getBroadcast(this, ALARM_COURSE_END, endCourseAlarmIntent, 0);
                courseAlarmManager.set(AlarmManager.RTC, startCourseAlarmCalendar.getTimeInMillis(), startCoursePendingIntent);
                courseAlarmManager.set(AlarmManager.RTC, endCourseAlarmCalendar.getTimeInMillis(), endCoursePendingIntent);
            } else {
                if (courseAlarmManager != null) {
                    courseAlarmManager.cancel(startCoursePendingIntent);
                    courseAlarmManager.cancel(endCoursePendingIntent);
                    startCoursePendingIntent.cancel();
                    endCoursePendingIntent.cancel();
                }

            }

            CourseEntity courseEntity = new CourseEntity(termID,
                    courseTitle, courseStartDate, courseEndDate, alarmEnabled,
                    courseStatus, courseMentorName, courseMentorPhone, courseMentorEmail);

            courseEntity.setId(courseID);
            courseViewModel.update(courseEntity);

            Toast.makeText(this, "Course updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Course not updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detailed_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detailed_course_menu_notes:
                Intent loadCourseNotes = new Intent(CourseActivity.this, CourseNotesListActivity.class);
                loadCourseNotes.putExtra(CourseNotesListActivity.EXTRA_COURSE_ID, courseID);
                loadCourseNotes.putExtra(CourseNotesListActivity.EXTRA_COURSE_TITLE, courseTitle);
                startActivity(loadCourseNotes);
                return true;
            case R.id.detailed_course_menu_assessments:
                Intent loadCourseAssessments = new Intent(CourseActivity.this, CourseAssessmentsListActivity.class);
                loadCourseAssessments.putExtra(CourseAssessmentsListActivity.EXTRA_COURSE_ID, courseID);
                loadCourseAssessments.putExtra(CourseAssessmentsListActivity.EXTRA_COURSE_TITLE, courseTitle);
                startActivity(loadCourseAssessments);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static String getStatus(int status) {
        String result;
        switch (status) {
            case STATUS_DROPPED:
                result = "Dropped";
                break;
            case STATUS_PLANNED:
                result = "Plan to take";
                break;
            case STATUS_IN_PROGRESS:
                result = "In progress";
                break;
            case STATUS_COMPLETED:
                result = "Completed";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }
}
