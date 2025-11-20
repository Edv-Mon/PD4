package com.example.pd4;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editContent;
    private Switch nativeFileSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);

        editName = findViewById(R.id.editName);
        editContent = findViewById(R.id.editContent);
        Button buttonSave = findViewById(R.id.buttonSave);
        nativeFileSwitch = findViewById(R.id.nativeFileSwitch);

        String noteName = getIntent().getStringExtra("NOTE_NAME");
        if (noteName != null) {
            loadNote(noteName);
        }

        buttonSave.setOnClickListener(v -> {
            String name = editName.getText().toString();
            String content = editContent.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a note name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nativeFileSwitch.isChecked()) {
                saveToFile(name, content);
            } else {
                saveToSharedPreferences(name, content);
            }
            finish();
        });
    }

    private void loadNote(String noteName) {
        editName.setText(noteName);
        
        // Check Shared Preferences first
        SharedPreferences sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE);
        if (sharedPreferences.contains(noteName)) {
            String content = sharedPreferences.getString(noteName, "");
            editContent.setText(content);
            nativeFileSwitch.setChecked(false);
            return;
        }

        // Check File
        if (Arrays.asList(fileList()).contains(noteName)) {
            try (FileInputStream fis = openFileInput(noteName);
                 InputStreamReader isr = new InputStreamReader(fis);
                 BufferedReader br = new BufferedReader(isr)) {
                StringBuilder sb = new StringBuilder();
                String text;
                while ((text = br.readLine()) != null) {
                    sb.append(text).append("\n");
                }
                // Remove last newline if exists
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
                editContent.setText(sb.toString());
                nativeFileSwitch.setChecked(true);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error loading file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToSharedPreferences(String name, String content) {
        SharedPreferences sharedPreferences = getSharedPreferences("Notes", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, content);
        editor.apply();
        Toast.makeText(this, "Saved to Shared Preferences", Toast.LENGTH_SHORT).show();
    }

    private void saveToFile(String name, String content) {
        try (FileOutputStream fos = openFileOutput(name, Context.MODE_PRIVATE)) {
            fos.write(content.getBytes());
            Toast.makeText(this, "Saved to File", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving file", Toast.LENGTH_SHORT).show();
        }
    }
}
