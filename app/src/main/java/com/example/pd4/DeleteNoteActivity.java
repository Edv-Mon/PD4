package com.example.pd4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;

public class DeleteNoteActivity extends AppCompatActivity {

    private Spinner spinnerSelect;
    private ArrayList<String> notesList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_note);

        spinnerSelect = findViewById(R.id.spinnerSelect);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        notesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, notesList);
        spinnerSelect.setAdapter(adapter);

        loadNotes();

        buttonDelete.setOnClickListener(v -> {
            Object selectedItem = spinnerSelect.getSelectedItem();
            if (selectedItem != null) {
                String noteToDelete = selectedItem.toString();
                deleteNote(noteToDelete);
            } else {
                Toast.makeText(this, "No note selected", Toast.LENGTH_SHORT).show();
            }
        });
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
                if (!notesList.contains(file)) {
                    notesList.add(file);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void deleteNote(String noteName) {
        boolean deleted = false;

        // Try to delete from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE);
        if (sharedPreferences.contains(noteName)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(noteName);
            editor.apply();
            deleted = true;
        }

        // Try to delete from Files
        if (deleteFile(noteName)) {
            deleted = true;
        }

        if (deleted) {
            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
            loadNotes(); // Refresh list
        } else {
            Toast.makeText(this, "Error deleting note", Toast.LENGTH_SHORT).show();
        }
    }
}
