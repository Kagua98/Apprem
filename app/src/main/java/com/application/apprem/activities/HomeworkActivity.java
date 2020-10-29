package com.application.apprem.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.application.apprem.R;
import com.application.apprem.adapters.HomeworkAdapter;
import com.application.apprem.models.Homework;
import com.application.apprem.utils.AlertDialogsHelper;
import com.application.apprem.utils.DbHelper;
import com.application.apprem.utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.Objects;

public class HomeworkActivity extends AppCompatActivity {
    public static final String ACTION_ADD_HOMEWORK = "addHomework";

    @NonNull
    private final AppCompatActivity context = this;
    private ListView listView;
    private HomeworkAdapter adapter;
    private DbHelper db;
    private int listPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(PreferenceUtil.getGeneralTheme(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homework);
        initAll();
        if (ACTION_ADD_HOMEWORK.equalsIgnoreCase(getIntent().getAction())) {
            findViewById(R.id.fab).performClick();
        }
    }

    private void initAll() {
        setupAdapter();
        setupListViewMultiSelect();
        setupCustomDialog();
    }

    private void setupAdapter() {
        db = new DbHelper(context);
        listView = findViewById(R.id.homeworklist);
        adapter = new HomeworkAdapter(HomeworkActivity.this, listView, R.layout.homework_child, db.getHomework());
        listView.setAdapter(adapter);
    }

    private void setupListViewMultiSelect() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(@NonNull ActionMode mode, int position, long id, boolean checked) {
                listPosition = position;
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " " + getResources().getString(R.string.selected));
                if (checkedCount == 0) mode.finish();
            }

            @Override
            public boolean onCreateActionMode(@NonNull ActionMode mode, Menu menu) {
                MenuInflater menuInflater = mode.getMenuInflater();
                menuInflater.inflate(R.menu.toolbar_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(@NonNull final ActionMode mode, @NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    ArrayList<Homework> removeList = new ArrayList<>();
                    SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
                    for (int i = 0; i < checkedItems.size(); i++) {
                        int key = checkedItems.keyAt(i);
                        if (checkedItems.get(key)) {
                            db.deleteHomeworkById(Objects.requireNonNull(adapter.getItem(key)));
                            removeList.add(adapter.getHomeworkList().get(key));
                        }
                    }
                    adapter.getHomeworkList().removeAll(removeList);
                    db.updateHomework(adapter.getHomework());
                    adapter.notifyDataSetChanged();
                    mode.finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    private void setupCustomDialog() {
        final View alertLayout = getLayoutInflater().inflate(R.layout.dialog_add_homework, null);
        AlertDialogsHelper.getAddHomeworkDialog(HomeworkActivity.this, alertLayout, adapter);
    }
}