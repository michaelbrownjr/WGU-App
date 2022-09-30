package activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.mbro.wguapp.R;
import entities.NoteEntity;
import viewmodel.NoteViewModel;

public class NoteActivity extends AppCompatActivity {
    public static final String EXTRA_NOTE_COURSE_ID =
            "com.mbro.wguapp.activities.NOTE_COURSE_ID";
    public static final String EXTRA_NOTE_COURSE_TITLE =
            "com.mbro.wguapp.activities.NOTE_COURSE_TITLE";
    public static final String EXTRA_NOTE_ID =
            "com.mbro.wguapp.activities.NOTE_ID";
    public static final String EXTRA_NOTE_TITLE =
            "com.mbro.wguapp.activities.NOTE_TITLE";
    public static final String EXTRA_NOTE_CONTENT =
            "activities.NOTE_CONTENT";

    public static final int EDIT_NOTE_REQUEST = 2;

    private NoteViewModel noteViewModel;

    private int courseID;
    private int noteID;
    private TextView textViewTitle;
    private TextView textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        setContentView(R.layout.activity_note);

        textViewTitle = findViewById(R.id.detailed_note_title);
        textViewContent = findViewById(R.id.detailed_note_content);

        Intent parentIntent = getIntent();
        courseID = parentIntent.getIntExtra(EXTRA_NOTE_COURSE_ID, -1);
        noteID = parentIntent.getIntExtra(EXTRA_NOTE_ID, -1);
        String noteTitle = parentIntent.getStringExtra(EXTRA_NOTE_TITLE);
        String courseTitle = parentIntent.getStringExtra(EXTRA_NOTE_COURSE_TITLE);

        setTitle(courseTitle + " | " + noteTitle);

        textViewTitle.setText(noteTitle);
        textViewContent.setText(parentIntent.getStringExtra(EXTRA_NOTE_CONTENT));

        FloatingActionButton buttonEditNote = findViewById(R.id.btn_edit_note);
        buttonEditNote.setOnClickListener(v -> {
            Intent editNoteIntent = new Intent(NoteActivity.this, AddEditNoteActivity.class);
            editNoteIntent.putExtra(AddEditNoteActivity.EXTRA_NOTE_ID, noteID);
            editNoteIntent.putExtra(AddEditNoteActivity.EXTRA_COURSE_ID, courseID);
            editNoteIntent.putExtra(AddEditNoteActivity.EXTRA_COURSE_NOTE_TITLE, noteTitle);
            editNoteIntent.putExtra(AddEditNoteActivity.EXTRA_COURSE_NOTE_CONTENT, textViewContent.getText().toString());
            startActivityForResult(editNoteIntent, EDIT_NOTE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            int noteID = data.getIntExtra(AddEditNoteActivity.EXTRA_NOTE_ID, -1);
            String noteName = data.getStringExtra(AddEditNoteActivity.EXTRA_COURSE_NOTE_TITLE);
            String noteContent = data.getStringExtra(AddEditNoteActivity.EXTRA_COURSE_NOTE_CONTENT);

            if(noteID == -1) {
                Toast.makeText(this, "Error, note not saved", Toast.LENGTH_SHORT).show();
                return;
            }

            textViewTitle.setText(noteName);
            textViewContent.setText(noteContent);

            NoteEntity noteEntity = new NoteEntity(courseID,
                    noteName, noteContent);

            noteEntity.setId(noteID);
            noteViewModel.update(noteEntity);

            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_share_text) {
            shareText();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void shareText() {
        Intent shareText = new Intent(Intent.ACTION_SEND);
        shareText.putExtra(Intent.EXTRA_TITLE, textViewTitle.getText().toString());
        shareText.putExtra(Intent.EXTRA_TEXT, textViewContent.getText().toString());
        shareText.setType("text/plain");
        startActivity(shareText);
    }
}
