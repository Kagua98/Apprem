package com.application.apprem.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import android.widget.Toast;


import com.application.apprem.R;
import com.application.apprem.models.Note;
import com.application.apprem.utils.DbHelper;
import com.application.apprem.utils.PreferenceUtil;


import java.util.Objects;

public class NoteEditorActivity extends AppCompatActivity {

    private DbHelper db;
    @Nullable
    private Note note;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_editor);
        setupIntent();
    }

    private void setupIntent() {
        db = new DbHelper(NoteEditorActivity.this);
        note = (Note) getIntent().getSerializableExtra(NotesActivity.KEY_NOTE);
        text = findViewById(R.id.edittextNote);
        if (Objects.requireNonNull(note).getText() != null) {
            text.setText(note.getText());
        }
    }

    @Override
    public void onBackPressed() {
        Objects.requireNonNull(note).setText(text.getText().toString());
        db.updateNote(note);


        Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT).show();


        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Objects.requireNonNull(note).setText(text.getText().toString());
                db.updateNote(note);

                Toast.makeText(NoteEditorActivity.this, getResources().getString(R.string.saved), Toast.LENGTH_SHORT)
                        .show();


                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
