package com.example.mylist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private Context context;
    private List<TaskItem> taskItemList;
    private TaskDatabaseHelper databaseHelper;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMM yyyy", new Locale("id"));
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-M-yyyy", new Locale("id"));
    Date date = null;
    String outputDateString = null;

    public TaskAdapter(Context context, List<TaskItem> taskItemList) {
        this.context = context;
        this.taskItemList = taskItemList;
        this.databaseHelper = new TaskDatabaseHelper(context);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tugas, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskItem taskItem = taskItemList.get(position);

        holder.titleTextView.setText(taskItem.getTitle());
        holder.descriptionTextView.setText(taskItem.getDescription());

        try {
            date = inputDateFormat.parse(taskItem.getDate());
            outputDateString = dateFormat.format(date);

            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.timeTextView.setText(taskItem.getTime());

        // Set status tugas
        if (taskItem.isCompleted()) {
            holder.statusTextView.setText("Selesai");
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            holder.statusTextView.setText("Belum Selesai");
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.main));
        }
    }


    @Override
    public int getItemCount() {
        return taskItemList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView, day, date, month, timeTextView, statusTextView;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            descriptionTextView = itemView.findViewById(R.id.description);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            timeTextView = itemView.findViewById(R.id.time);
            statusTextView = itemView.findViewById(R.id.status);

            descriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDescriptionPopup(descriptionTextView.getText().toString());
                }
            });

            ImageView optionsImageView = itemView.findViewById(R.id.options);
            optionsImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopupMenu(optionsImageView);
                }
            });

        }

        private void showDescriptionPopup(String description) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Detail Deskripsi")
                    .setMessage(description)
                    .setPositiveButton("OK", null)
                    .show();
        }

        private void showPopupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menuDelete:
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                TaskItem taskItem = taskItemList.get(position);
                                showDeleteConfirmationDialog(taskItem.getId(), position);
                            }
                            return true;
                        case R.id.menuEdit:
                            int editPosition = getAdapterPosition();
                            if (editPosition != RecyclerView.NO_POSITION) {
                                TaskItem taskItem = taskItemList.get(editPosition);
                                editTask(taskItem);
                            }
                            return true;
                        case R.id.menuComplete:
                            // Implement complete action
                            toggleTaskCompletion(getAdapterPosition());
                            return true;
                        case R.id.menuAlarm:
                            int alarmPosition = getAdapterPosition();
                            if (alarmPosition != RecyclerView.NO_POSITION) {
                                TaskItem taskItem = taskItemList.get(alarmPosition);
                                setAlarmForTask(taskItem);
                            }
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popupMenu.show();
        }

        private void setAlarmForTask(TaskItem taskItem) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) {
                return;
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("task_title", taskItem.getTitle()); // Data yang akan digunakan saat alarm berbunyi
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-M-yyyy HH:mm", Locale.getDefault());
            String dateTimeString = taskItem.getDate() + " " + taskItem.getTime();
            try {
                Date dateTime = dateFormat.parse(dateTimeString);
                long alarmTimeMillis = dateTime.getTime(); // Waktu dalam bentuk milidetik

                if (alarmTimeMillis > System.currentTimeMillis()) {
                    // Set alarm
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeMillis, pendingIntent);
                    }

                    Toast.makeText(context, "Alarm diatur untuk tugas: " + taskItem.getTitle(), Toast.LENGTH_SHORT).show();
                } else {
                    // Tampilkan pesan jika waktu alarm sudah lewat
                    Toast.makeText(context, "Waktu tugas sudah lewat. Tidak bisa mengatur alarm.", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        private void toggleTaskCompletion(int position) {
            TaskItem taskItem = taskItemList.get(position);
            boolean isCompleted = !taskItem.isCompleted();

            // Batalkan alarm jika tugas menjadi selesai
            if (isCompleted) {
                cancelAlarmForTask(taskItem);
            }
            taskItem.setCompleted(!taskItem.isCompleted());
            notifyItemChanged(position);
        }

        private void showDeleteConfirmationDialog(final int taskId, final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Konfirmasi Hapus");
            builder.setMessage("Apakah Anda yakin ingin menghapus tugas ini?");
            builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteTask(taskId, position);
                }
            });
            builder.setNegativeButton("Batal", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        private void deleteTask(int taskId, int position) {
            // Batalkan alarm sebelum menghapus tugas
            TaskItem taskItem = taskItemList.get(position);
            cancelAlarmForTask(taskItem);

            databaseHelper.deleteTask(taskId);
            taskItemList.remove(position);
            notifyItemRemoved(position);
        }

        private void editTask(TaskItem taskItem) {
            Intent intent = new Intent(context, EditTaskActivity.class);
            intent.putExtra("task_id", taskItem.getId());
            context.startActivity(intent);
        }

        private void cancelAlarmForTask(TaskItem taskItem) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskItem.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
