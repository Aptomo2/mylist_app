package com.example.mylist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView addTaskButton;
    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private List<TaskItem> taskItemList = new ArrayList<>(); // Inisialisasi taskItemList
    private TaskDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addTaskButton = findViewById(R.id.addTask);
        taskRecyclerView = findViewById(R.id.taskRecycler);
        databaseHelper = new TaskDatabaseHelper(this);

        ImageView emptyImageView = findViewById(R.id.noDataImage);

            // Menampilkan gambar GIF menggunakan Glide
            Glide.with(this)
                    .asGif()
                    .load(R.drawable.anim)
                    .into(emptyImageView);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivity(intent);
            }
        });

        taskAdapter = new TaskAdapter(this, databaseHelper.getAllTasks());
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        TextView aboutTextView = findViewById(R.id.about);
        aboutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        taskAdapter = new TaskAdapter(this, databaseHelper.getAllTasks());
        taskRecyclerView.setAdapter(taskAdapter);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void deleteTask(int taskId) {
        databaseHelper.deleteTask(taskId);
        refreshTaskList();
        Toast.makeText(this, "Tugas berhasil dihapus", Toast.LENGTH_SHORT).show();
    }

    private void refreshTaskList() {
        taskItemList.clear();
        taskItemList.addAll(databaseHelper.getAllTasks());
        taskAdapter.notifyDataSetChanged();

    }

}
