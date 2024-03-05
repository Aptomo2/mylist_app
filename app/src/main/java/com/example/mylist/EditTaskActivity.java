package com.example.mylist;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText editTaskTitle, editTaskDescription, editTaskDate, editTaskTime;
    private Button btnSave;
    private TaskDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_tugas);

        editTaskTitle = findViewById(R.id.addTaskTitle);
        editTaskDescription = findViewById(R.id.addTaskDescription);
        editTaskDate = findViewById(R.id.taskDate);
        editTaskTime = findViewById(R.id.taskTime);
        btnSave = findViewById(R.id.addTask);
        databaseHelper = new TaskDatabaseHelper(this);

        // Mengambil ID tugas dari intent
        int taskId = getIntent().getIntExtra("task_id", -1);

        // Lakukan query ke database untuk mendapatkan tugas berdasarkan ID
        TaskItem taskItem = databaseHelper.getTaskById(taskId);

        // Mengisi input dengan data tugas yang ada
        editTaskTitle.setText(taskItem.getTitle());
        editTaskDescription.setText(taskItem.getDescription());
        editTaskDate.setText(taskItem.getDate());
        editTaskTime.setText(taskItem.getTime());

        editTaskDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showDatePicker();
                }
            }
        });

        editTaskTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showTimePicker();
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update data tugas di database
                updateTask(taskId);
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year);
                        editTaskDate.setText(selectedDate);
                    }
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                        editTaskTime.setText(selectedTime);
                    }
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateTask(int taskId) {
        String newTitle = editTaskTitle.getText().toString().trim();
        String newDescription = editTaskDescription.getText().toString().trim();
        String newDate = editTaskDate.getText().toString().trim();
        String newTime = editTaskTime.getText().toString().trim();

        if (newTitle.isEmpty() || newDate.isEmpty() || newTime.isEmpty()) {
            Toast.makeText(this, "Harap isi semua bidang", Toast.LENGTH_SHORT).show();
            return;
        }

        TaskItem updatedTask = new TaskItem(taskId, newTitle, newDescription, newDate, newTime);

        // Update task in the database
        if (databaseHelper.updateTask(updatedTask)) {
            Toast.makeText(this, "Tugas berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish(); // Kembali ke tampilan sebelumnya setelah mengupdate tugas
        } else {
            Toast.makeText(this, "Gagal memperbarui tugas", Toast.LENGTH_SHORT).show();
        }
    }
}
