package activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.mbro.wguapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddEditAssessmentActivity extends AppCompatActivity {
    public static final String EXTRA_ASSESSMENT_ID =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_ID";
    public static final String EXTRA_COURSE_ID =
            "com.mbro.wguapp.activities.COURSE__ID";
    public static final String EXTRA_COURSE_ASSESSMENT_TITLE =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_TITLE";
    public static final String EXTRA_COURSE_ASSESSMENT_GOAL_DATE =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_GOAL_DATE";
    public static final String EXTRA_COURSE_ASSESSMENT_ALERT =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_ALERT";
    public static final String EXTRA_COURSE_ASSESSMENT_START_DATE =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_START_DATE";
    public static final String EXTRA_COURSE_ASSESSMENT_ALERT_START =
            "com.mbro.wguapp.activities.COURSE_ASSESSMENT_ALERT_START";
    public static final String EXTRA_ASSESSMENT_TYPE =
            "com.mbro.wguapp.activities.ASSESSMENT_TYPE";

    public static final int EDIT_ASSESSMENT_REQUEST = 2;

    public static final String DATE_FORMAT = "MM/dd/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    private Calendar calendarGoalDate;
    private Calendar calendarStartDate;

    private EditText editTextTitle;
    private RadioGroup editRadioGroupType;

    private EditText editTextGoalDate;
    private CheckBox editCheckboxAlarmEnabled;

    private EditText editTextStartDate;
    private CheckBox editCheckboxStartAlarmEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_assessment);

        editTextTitle = findViewById(R.id.edit_assessment_name);
        editRadioGroupType = findViewById(R.id.edit_assessment_radio_type);

        editTextStartDate = findViewById(R.id.edit_assessment_start_date);
        editTextGoalDate = findViewById(R.id.edit_assessment_goal_date);

        editCheckboxStartAlarmEnabled = findViewById(R.id.edit_assessment_start_alarm);
        editCheckboxAlarmEnabled = findViewById(R.id.edit_assessment_alarm);


        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        this.calendarGoalDate = Calendar.getInstance();
        this.calendarStartDate = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetGoal = (view, year, month, dayOfMonth) -> {
            calendarGoalDate.set(Calendar.YEAR, year);
            calendarGoalDate.set(Calendar.MONTH, month);
            calendarGoalDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editTextGoalDate.setText(sdf.format(calendarGoalDate.getTime()));
        };
        DatePickerDialog.OnDateSetListener dateSetStart = (view, year, month, dayOfMonth) -> {
            calendarStartDate.set(Calendar.YEAR, year);
            calendarStartDate.set(Calendar.MONTH, month);
            calendarStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editTextStartDate.setText(sdf.format(calendarStartDate.getTime()));
        };

        editTextGoalDate.setOnClickListener(v -> new DatePickerDialog(AddEditAssessmentActivity.this,
            dateSetGoal,
            calendarGoalDate.get(Calendar.YEAR),
            calendarGoalDate.get(Calendar.MONTH),
            calendarGoalDate.get(Calendar.DAY_OF_MONTH)).show());

        editTextStartDate.setOnClickListener(v -> new DatePickerDialog(AddEditAssessmentActivity.this,
                dateSetStart,
                calendarStartDate.get(Calendar.YEAR),
                calendarStartDate.get(Calendar.MONTH),
                calendarStartDate.get(Calendar.DAY_OF_MONTH)).show());

        Intent parentIntent = getIntent();
        if(parentIntent.hasExtra(EXTRA_ASSESSMENT_ID)) {
            setTitle("Edit Assessment");
            editTextTitle.setText(parentIntent.getStringExtra(EXTRA_COURSE_ASSESSMENT_TITLE));
            editTextGoalDate.setText(parentIntent.getStringExtra(EXTRA_COURSE_ASSESSMENT_GOAL_DATE));
            editTextStartDate.setText(parentIntent.getStringExtra(EXTRA_COURSE_ASSESSMENT_START_DATE));
            editRadioGroupType.check(getTypeBtnID(parentIntent.getIntExtra(EXTRA_ASSESSMENT_TYPE, -1)));
            if(parentIntent.getBooleanExtra(EXTRA_COURSE_ASSESSMENT_ALERT, false))
                editCheckboxAlarmEnabled.performClick();
            if (parentIntent.getBooleanExtra(EXTRA_COURSE_ASSESSMENT_ALERT_START, false))
                editCheckboxStartAlarmEnabled.performClick();
        } else {
            setTitle("Add Assessment");
        }

    }

    private void saveAssessment() {
        String assessmentTitle = editTextTitle.getText().toString();
        String assessmentGoalDate = editTextGoalDate.getText().toString();
        String assessmentStartDate = editTextStartDate.getText().toString();

        boolean alarmEnabled = editCheckboxAlarmEnabled.isChecked();
        boolean startAlarmEnabled = editCheckboxStartAlarmEnabled.isChecked();

        if(assessmentTitle.trim().isEmpty()
            || assessmentGoalDate.trim().isEmpty() || assessmentStartDate.trim().isEmpty()
            || getTypeBtnID(editRadioGroupType.getCheckedRadioButtonId()) != -1) {
            Toast.makeText(this, "Assessment not saved. Empty fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_COURSE_ASSESSMENT_TITLE, assessmentTitle);
        data.putExtra(EXTRA_ASSESSMENT_TYPE, getRadioType(editRadioGroupType.getCheckedRadioButtonId()));
        data.putExtra(EXTRA_COURSE_ASSESSMENT_GOAL_DATE, assessmentGoalDate);
        data.putExtra(EXTRA_COURSE_ASSESSMENT_ALERT, alarmEnabled);
        data.putExtra(EXTRA_COURSE_ASSESSMENT_START_DATE, assessmentStartDate);
        data.putExtra(EXTRA_COURSE_ASSESSMENT_ALERT_START, startAlarmEnabled);
        int assessmentID = getIntent().getIntExtra(EXTRA_ASSESSMENT_ID, -1);
        if(assessmentID != -1)
            data.putExtra(EXTRA_ASSESSMENT_ID, assessmentID);

        setResult(RESULT_OK, data);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add_edit_save) {
            saveAssessment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private int getTypeBtnID(int id) {
        int btn_id;
        switch (id) {
            case AssessmentActivity.TYPE_PA:
                btn_id = R.id.radio_assessment_type_pa;
                break;
            case AssessmentActivity.TYPE_OA:
                btn_id = R.id.radio_assessment_type_oa;
                break;
            default:
                btn_id = -1;
        }
        return btn_id;
    }

    @SuppressLint("NonConstantResourceId")
    private int getRadioType(int btnID) {
        int typeID;
        switch (btnID) {
            case R.id.radio_assessment_type_pa:
                typeID = AssessmentActivity.TYPE_PA;
                break;
            case R.id.radio_assessment_type_oa:
                typeID = AssessmentActivity.TYPE_OA;
                break;
            default:
                typeID = -1;
        }
        return typeID;
    }

//    private void updateLabel() {
//        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
//
//        editTextGoalDate.setText(sdf.format(calendarGoalDate.getTime()));
//    }

}
