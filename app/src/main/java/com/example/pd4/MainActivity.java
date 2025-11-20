package com.example.pd4;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> notesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.listViewNotes);
        notesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String noteName = notesList.get(position);
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            intent.putExtra("NOTE_NAME", noteName);
            startActivity(intent);
        });

        Button addNoteButton = findViewById(R.id.addNote);
        addNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivity(intent);
        });

        Button deleteNoteButton = findViewById(R.id.deleteNote);
        deleteNoteButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DeleteNoteActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        notesList.clear();

        // Load from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();
        if (allEntries != null) {
            for (String key : allEntries.keySet()) {
                notesList.add(key);
            }
        }

        // Load from Files
        String[] files = fileList();
        if (files != null) {
            for (String file : files) {
                // Exclude internal android directories/files if necessary, but generally
                // fileList() returns files created by openFileOutput/createNewFile in the files dir.
                // Instant Run or others might add files, but for this scope we assume user files.
                if (!notesList.contains(file)) { // Avoid duplicates if file and sharedpref have same name (unlikely but possible logic)
                     notesList.add(file);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }
}
