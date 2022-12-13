package activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.mbro.wguapp.R;

import java.util.ArrayList;
import java.util.List;

import adapters.CourseAdapter;
import entities.CourseEntity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button viewTermListButton = findViewById(R.id.view_term_list_btn);



        viewTermListButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TermListActivity.class);
            v.getContext().startActivity(intent);
        });
    }
}