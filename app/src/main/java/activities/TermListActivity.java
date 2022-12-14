package activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mbro.wguapp.R;
import adapters.TermAdapter;
import entities.TermEntity;
import viewmodel.CourseViewModel;
import viewmodel.TermViewModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


public class TermListActivity extends AppCompatActivity {
    public static final int ADD_TERM_REQUEST = 1;

    private TermViewModel termViewModel;
    private TermAdapter adapter;
    private ArrayList<TermEntity> TermEntityArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_list);

        FloatingActionButton buttonAddTerm = findViewById(R.id.fab_add_term);
        buttonAddTerm.setOnClickListener(v -> {
            Intent intent = new Intent(TermListActivity.this, AddEditTermActivity.class);
            startActivityForResult(intent, ADD_TERM_REQUEST);
        });
        TermRecyclerView();

    }

    private void TermRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.termListView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final TermAdapter adapter = new TermAdapter();
        recyclerView.setAdapter(adapter);

        termViewModel = new ViewModelProvider(this).get(TermViewModel.class);
        termViewModel.getAllTerms().observe(this, adapter::setTerms);

        CourseViewModel courseViewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            //Included function to prevent deleting Terms with Courses associated with it
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                TermEntity deletedTerm = adapter.getTermAt(viewHolder.getAdapterPosition());

                int relatedCourses = 0;
                try {
                    relatedCourses = courseViewModel.getTermCourses(deletedTerm.getId()).size();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }


                if(relatedCourses > 0) {
                    Toast.makeText(TermListActivity.this, "Courses still attached. Term not deleted!", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                } else {
                    termViewModel.delete(deletedTerm);
                    Toast.makeText(TermListActivity.this, "Term deleted", Toast.LENGTH_SHORT).show();
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(termEntity -> {
            Intent intent = new Intent(TermListActivity.this, TermActivity.class);
            intent.putExtra(TermActivity.EXTRA_ID, termEntity.getId());
            intent.putExtra(TermActivity.EXTRA_TITLE, termEntity.getTitle());
            intent.putExtra(TermActivity.EXTRA_START_DATE, termEntity.getStart());
            intent.putExtra(TermActivity.EXTRA_END_DATE, termEntity.getEnd());
            startActivity(intent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ADD_TERM_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            String title = data.getStringExtra(AddEditTermActivity.EXTRA_TERM_TITLE);
            String startDate = data.getStringExtra(AddEditTermActivity.EXTRA_TERM_START_DATE);
            String endDate = data.getStringExtra(AddEditTermActivity.EXTRA_TERM_END_DATE);

            TermEntity termEntity = new TermEntity(title, startDate, endDate);
            termViewModel.insert(termEntity);

            Toast.makeText(this, "Term added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Term not added", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // below line is to get out inflater
        MenuInflater inflater = getMenuInflater();

        // inside inflater we are inflating our new file
        inflater.inflate(R.menu.search_menu, menu);

        // below line is to get out menu item
        MenuItem searchItem = menu.findItem(R.id.actionSearch);

        // getting search view of our item
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchedText) {
                filter(searchedText);
                return false;
            }
        });
        return true;
    }

    private void filter(String text){
        // create a new array list to filter our data
        ArrayList<TermEntity> filteredList = new ArrayList<TermEntity>();

        // run a for lop to compare elements
        for (TermEntity item : TermEntityArrayList){
            // check if the entered text matches with an item in the recycler view
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())){
                // if the item matches an item in the list add it to the filtered list
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()){
            // if no item is found in the list display toast message
           Toast.makeText(this, "No Term Found!", Toast.LENGTH_SHORT).show();
        } else {
            // pass the filtered list to our adapter class
            adapter.setTerms(filteredList);

        }
    }

}
