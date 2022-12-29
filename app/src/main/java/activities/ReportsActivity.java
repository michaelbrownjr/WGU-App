package activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mbro.wguapp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import adapters.CourseAdapter;
import adapters.TermAdapter;
import viewmodel.CourseViewModel;
import viewmodel.TermViewModel;

public class ReportsActivity extends AppCompatActivity {
    private TermAdapter adapterTerm = new TermAdapter();
    private CourseAdapter adapterCourse = new CourseAdapter();
    private Date date;
    private TermViewModel termViewModel;
    private CourseViewModel courseViewModel;
    private int numTerms = 0;
    private int numCourses = 0;

    public static final String DATE_FORMAT = "MM/dd/yyyy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Setting the Date
        date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a z", Locale.US);
        String formattedDate = format.format(date);

        // Setting the Terms
        TextView textViewTotalTerms = findViewById(R.id.total_terms);
        TextView textViewNumTerms = findViewById(R.id.number_of_terms);

        termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        termViewModel.getAllTerms().observe(this, terms->{
            adapterTerm.setTerms(terms);
            numTerms = terms.size();
            textViewNumTerms.setText(String.valueOf(numTerms));
        });

        // Setting the Courses
        TextView textViewTotalCourses = findViewById(R.id.total_courses);
        TextView textViewNumCourses = findViewById(R.id.number_of_courses);

        courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        courseViewModel.getAllCourses().observe(this, courses->{
            adapterCourse.setCourses(courses);
            numCourses = courses.size();
            textViewNumCourses.setText(String.valueOf(numCourses));
        });

        TextView textViewCurrentDate = findViewById(R.id.current_date);
        TextView textViewDate = findViewById(R.id.date);
        textViewDate.setText(formattedDate);



    }
}
