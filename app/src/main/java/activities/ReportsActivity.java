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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        // Setting the Date
        date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        String formattedDate = format.format(date);

        // Setting the Terms
        TextView textViewTotalTerms = findViewById(R.id.total_terms);
        TextView textViewNumTerms = findViewById(R.id.number_of_terms);

        termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        termViewModel.getAllTerms().observe(this, terms->{
            adapterTerm.setTerms(terms);
        });

        int numTerms = adapterTerm.getItemCount();
        textViewNumTerms.setText(String.valueOf(numTerms));

        TextView textViewTotalCourses = findViewById(R.id.total_courses);
        TextView textViewNumCourses = findViewById(R.id.number_of_courses);

//        int numCourses = adapterCourse.getItemCount();
//        textViewNumCourses.setText(String.valueOf(numCourses));

        TextView textViewCurrentDate = findViewById(R.id.current_date);
        TextView textViewDate = findViewById(R.id.date);
        textViewDate.setText(formattedDate);



    }
}
