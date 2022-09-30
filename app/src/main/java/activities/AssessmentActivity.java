package activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mbro.wguapp.R;
import alarms.AssessmentAlarmReceiver;
import entities.AssessmentEntity;
import viewmodel.AssessmentViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AssessmentActivity extends AppCompatActivity {
    public static final String EXTRA_ASSESSMENT_COURSE_ID =
            "com.mbro.wguapp.activities.ASSESSMENT_COURSE_ID";
    public static final String EXTRA_ASSESSMENT_COURSE_TITLE =
            "com.mbro.wguapp.activities.ASSESSMENT_COURSE_TITLE";
    public static final String EXTRA_ASSESSMENT_ID =
            "com.mbro.wguapp.activities.ASSESSMENT_ID";
    public static final String EXTRA_ASSESSMENT_TITLE =
            "com.mbro.wguapp.activities.ASSESSMENT_TITLE";
    public static final String EXTRA_ASSESSMENT_DUE_DATE =
            "com.mbro.wguapp.activities.ASSESSMENT_DUE_DATE";
    public static final String EXTRA_ASSESSMENT_ALARM =
            "com.mbro.wguapp.activities.ASSESSMENT_ALARM";
    public static final String EXTRA_ASSESSMENT_TYPE =
            "com.mbro.wguapp.activities.ASSESSMENT_TYPE";
    public static final int TYPE_PA = 0;
    public static final int TYPE_OA = 1;

    private AlarmManager assessmentAlarmManager;
    private PendingIntent goalAssessmentAlarmIntent;

    private AssessmentViewModel assessmentViewModel;

    private int courseID;
    private int assessmentID;
    private TextView textViewTitle;
    private TextView textViewDueDate;
    private TextView textViewType;
    private ImageView imageViewAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assessmentViewModel = new ViewModelProvider(this).get(AssessmentViewModel.class);
        setContentView(R.layout.activity_assessment);

        textViewTitle = findViewById(R.id.detailed_assessment_title);
        textViewDueDate = findViewById(R.id.detailed_assessment_due_date);
        textViewType = findViewById(R.id.detailed_assessment_type);
        imageViewAlarm = findViewById(R.id.detailed_assessment_image_alarm);

        Intent parentIntent = getIntent();
        courseID = parentIntent.getIntExtra(EXTRA_ASSESSMENT_COURSE_ID, -1);
        assessmentID = parentIntent.getIntExtra(EXTRA_ASSESSMENT_ID, -1);
        String courseTitle = parentIntent.getStringExtra(EXTRA_ASSESSMENT_COURSE_TITLE);
        String assessmentTitle = parentIntent.getStringExtra(EXTRA_ASSESSMENT_TITLE);
        boolean alarmEnabled = parentIntent.getBooleanExtra(EXTRA_ASSESSMENT_ALARM, false);
        if(alarmEnabled) {
            imageViewAlarm.setVisibility(View.VISIBLE);
        } else {
            imageViewAlarm.setVisibility(View.INVISIBLE);
        }

        setTitle(courseTitle + " | " + assessmentTitle);

        textViewTitle.setText(assessmentTitle);
        textViewDueDate.setText(parentIntent.getStringExtra(EXTRA_ASSESSMENT_DUE_DATE));
        int type = parentIntent.getIntExtra(EXTRA_ASSESSMENT_TYPE, -1);
        textViewType.setText(getAssessmentType(type));

        editAssessment(parentIntent, assessmentTitle, type);
    }

    private void editAssessment(Intent parentIntent, String assessmentTitle, int type) {
        FloatingActionButton buttonEditAssessment = findViewById(R.id.btn_edit_assessment);
        buttonEditAssessment.setOnClickListener(v -> {
            Intent editAssessmentIntent = new Intent(AssessmentActivity.this, AddEditAssessmentActivity.class);
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_ASSESSMENT_ID, assessmentID);
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_COURSE_ID, courseID);
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_TITLE, assessmentTitle);
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_GOAL_DATE, parentIntent.getStringExtra(EXTRA_ASSESSMENT_DUE_DATE));
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_ALERT, parentIntent.getBooleanExtra(EXTRA_ASSESSMENT_ALARM, false));
            editAssessmentIntent.putExtra(AddEditAssessmentActivity.EXTRA_ASSESSMENT_TYPE, type);
            startActivityForResult(editAssessmentIntent, AddEditAssessmentActivity.EDIT_ASSESSMENT_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AddEditAssessmentActivity.EDIT_ASSESSMENT_REQUEST && resultCode == RESULT_OK) {
            int assessmentID = data.getIntExtra(AddEditAssessmentActivity.EXTRA_ASSESSMENT_ID, -1);
            String assessmentName = data.getStringExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_TITLE);
            int assessmentType = data.getIntExtra(AddEditAssessmentActivity.EXTRA_ASSESSMENT_TYPE, -1);
            String assessmentGoalDate = data.getStringExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_GOAL_DATE);
            boolean assessmentAlertEnabled = data.getBooleanExtra(AddEditAssessmentActivity.EXTRA_COURSE_ASSESSMENT_ALERT, false);

            if( assessmentID == -1
                    || assessmentType == -1) {
                Toast.makeText(this, "Error, assessment not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            textViewTitle.setText(assessmentName);
            textViewDueDate.setText(assessmentGoalDate);
            textViewType.setText(getAssessmentType(assessmentType));
            if (assessmentAlertEnabled) {
                imageViewAlarm.setVisibility(View.VISIBLE);
            } else {
                imageViewAlarm.setVisibility(View.INVISIBLE);
            }

            if(assessmentAlertEnabled) {
                assessmentAlarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
                Intent goalAssessmentAlarmIntent = new Intent(this, AssessmentAlarmReceiver.class);
                goalAssessmentAlarmIntent.putExtra(AssessmentAlarmReceiver.ALARM_NOTIFICATION_TITLE, assessmentName);
                goalAssessmentAlarmIntent.putExtra(AssessmentAlarmReceiver.ALARM_NOTIFICATION_ASSESSMENT_TYPE, getAssessmentType(assessmentType));
                goalAssessmentAlarmIntent.putExtra(AssessmentAlarmReceiver.ALARM_NOTIFICATION_TEXT, assessmentGoalDate);
                PendingIntent goalAssessmentPendingAlert = PendingIntent.getBroadcast(this, 0, goalAssessmentAlarmIntent, 0);

                Calendar goalAssessmentCalendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat(AddEditAssessmentActivity.DATE_FORMAT, Locale.ENGLISH);
                try {
                    goalAssessmentCalendar.setTime(Objects.requireNonNull(dateFormat.parse(assessmentGoalDate)));
                    goalAssessmentCalendar.set(Calendar.HOUR, 8);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                assessmentAlarmManager.set(AlarmManager.RTC, goalAssessmentCalendar.getTimeInMillis(), goalAssessmentPendingAlert);
            } else {
                if(assessmentAlarmManager != null) {
                    assessmentAlarmManager.cancel(goalAssessmentAlarmIntent);
                    goalAssessmentAlarmIntent.cancel();
                }

            }

            AssessmentEntity assessmentEntity = new AssessmentEntity(courseID,
                    assessmentName, assessmentType, assessmentGoalDate, assessmentAlertEnabled);

            assessmentEntity.setId(assessmentID);
            assessmentViewModel.update(assessmentEntity);

            Toast.makeText(this, "Assessment updated", Toast.LENGTH_SHORT).show();
        }  else {
            Toast.makeText(this, "Assessment not updated", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getAssessmentType(int type) {
        String result;
        switch (type) {
            case TYPE_PA:
                result = "PA";
                break;
            case TYPE_OA:
                result = "OA";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }
}
