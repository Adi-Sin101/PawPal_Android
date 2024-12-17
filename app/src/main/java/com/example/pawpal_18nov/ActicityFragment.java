package com.example.pawpal_18nov;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ActicityFragment extends Fragment {

    private LinearLayout taskContainer;
    private AlertDialog addTaskDialog;
    private DatabaseReference databaseReference;
    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    private String userId;
    private Spinner reminderSpinner;

    @Override
    public void onStart() {
        super.onStart();
        databaseReference = FirebaseDatabase.getInstance().getReference(userId + "/tasks");
        loadTasksFromFirebase();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_acticity, container, false);
        taskContainer = view.findViewById(R.id.container);
        Button addButton = view.findViewById(R.id.add);
        buildAddTaskDialog();
        addButton.setOnClickListener(v -> addTaskDialog.show());
        return view;
    }



    private void buildAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText taskInput = dialogView.findViewById(R.id.nameEdit);
        Button btnDate = dialogView.findViewById(R.id.btnDate);
        Button btnTime = dialogView.findViewById(R.id.btnTime);
        reminderSpinner = dialogView.findViewById(R.id.reminderSpinner);

        // Initialize calendar and default values
        final Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedHour = calendar.get(Calendar.HOUR_OF_DAY);
        selectedMinute = calendar.get(Calendar.MINUTE);

        // Set initial date and time to current date and time
        btnDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
        btnTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

        // Date picker dialog
        btnDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                btnDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
            }, selectedYear, selectedMonth, selectedDay);
            datePickerDialog.show();
        });

        // Time picker dialog
        btnTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                btnTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, selectedHour, selectedMinute, true);
            timePickerDialog.show();
        });

        builder.setView(dialogView)
                .setTitle("Enter Task")
                .setPositiveButton("Save", (dialog, which) -> {
                    String taskName = taskInput.getText().toString().trim();
                    if (!taskName.isEmpty()) {
                        saveTaskToFirebase(taskName);
                        taskInput.setText("");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        addTaskDialog = builder.create();
    }

    private void saveTaskToFirebase(String taskName) {
        String taskId = databaseReference.push().getKey();
        if (taskId != null) {
            Map<String, String> task = new HashMap<>();
            task.put("id", taskId);
            task.put("name", taskName);
            task.put("date", String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)); // Save correct date
            task.put("time", String.format("%02d:%02d", selectedHour, selectedMinute)); // Save correct time

            databaseReference.child(taskId).setValue(task);

            // Reminder logic (if necessary)
            if (!reminderSpinner.getSelectedItem().toString().equals("Don't remind me")) {
                long reminderOffset = getReminderTime(reminderSpinner.getSelectedItem().toString());

                Calendar reminderTime = Calendar.getInstance();
                reminderTime.set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute, 0);
                reminderTime.add(Calendar.MILLISECOND, -(int) reminderOffset);

                scheduleReminder(taskName, reminderTime);
            }
        }
    }

    private void loadTasksFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskContainer.removeAllViews();

                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    String taskId = taskSnapshot.child("id").getValue(String.class);
                    String taskName = taskSnapshot.child("name").getValue(String.class);
                    String taskDate = taskSnapshot.child("date").getValue(String.class);
                    String taskTime = taskSnapshot.child("time").getValue(String.class);

                    if (taskId != null && taskName != null && taskDate != null && taskTime != null) {
                        addTaskCard(taskId, taskName, taskDate, taskTime);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void addTaskCard(String taskId, String taskName, String taskDate, String taskTime) {
        View cardView = getLayoutInflater().inflate(R.layout.card, null);

        TextView taskNameView = cardView.findViewById(R.id.taskName);
        taskNameView.setText(taskName);

        TextView taskDateView = cardView.findViewById(R.id.taskDate);
        taskDateView.setText(taskDate);

        TextView taskTimeView = cardView.findViewById(R.id.taskTime);
        taskTimeView.setText(taskTime);

        Button editButton = cardView.findViewById(R.id.editTask);
        editButton.setOnClickListener(v -> showEditTaskDialog(taskId, taskName, taskDate, taskTime));

        Button deleteButton = cardView.findViewById(R.id.deleteTask);
        deleteButton.setOnClickListener(v -> deleteTaskFromFirebase(taskId, cardView));

        taskContainer.addView(cardView);
    }

    private void showEditTaskDialog(String taskId, String currentTaskName, String currentTaskDate, String currentTaskTime) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog, null);

        final EditText taskInput = dialogView.findViewById(R.id.nameEdit);
        Button btnDate = dialogView.findViewById(R.id.btnDate);
        Button btnTime = dialogView.findViewById(R.id.btnTime);
        reminderSpinner = dialogView.findViewById(R.id.reminderSpinner);

        // Set current task values
        taskInput.setText(currentTaskName);
        String[] dateParts = currentTaskDate.split("/");
        selectedDay = Integer.parseInt(dateParts[0]);
        selectedMonth = Integer.parseInt(dateParts[1]) - 1; // Adjust month as DatePicker uses 0-based months
        selectedYear = Integer.parseInt(dateParts[2]);

        String[] timeParts = currentTaskTime.split(":");
        selectedHour = Integer.parseInt(timeParts[0]);
        selectedMinute = Integer.parseInt(timeParts[1]);

        // Set initial date and time
        btnDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
        btnTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));

        // Date picker dialog
        btnDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                selectedYear = year;
                selectedMonth = month;
                selectedDay = dayOfMonth;
                btnDate.setText(String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
            }, selectedYear, selectedMonth, selectedDay);
            datePickerDialog.show();
        });

        // Time picker dialog
        btnTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                btnTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }, selectedHour, selectedMinute, true);
            timePickerDialog.show();
        });

        builder.setView(dialogView)
                .setTitle("Edit Task")
                .setPositiveButton("Save", (dialog, which) -> {
                    String updatedTaskName = taskInput.getText().toString().trim();
                    if (!updatedTaskName.isEmpty()) {
                        updateTaskInFirebase(taskId, updatedTaskName);
                        taskInput.setText("");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {});

        addTaskDialog = builder.create();
        addTaskDialog.show();
    }
    private void updateTaskInFirebase(String taskId, String updatedTaskName) {
        Map<String, Object> updatedTask = new HashMap<>();
        updatedTask.put("name", updatedTaskName);
        updatedTask.put("date", String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear));
        updatedTask.put("time", String.format("%02d:%02d", selectedHour, selectedMinute));

        // Update the task in Firebase
        databaseReference.child(taskId).updateChildren(updatedTask);

        // Refresh the task list
        loadTasksFromFirebase();
    }




    private void deleteTaskFromFirebase(String taskId, View cardView) {
        databaseReference.child(taskId).removeValue();
        taskContainer.removeView(cardView);
    }

    private long getReminderTime(String selectedReminder) {
        switch (selectedReminder) {
            case "At time of event":
                return 0;
            case "5 minutes before":
                return 5 * 60 * 1000;
            case "15 minutes before":
                return 15 * 60 * 1000;
            case "30 minutes before":
                return 30 * 60 * 1000;
            case "1 hour before":
                return 60 * 60 * 1000;
            case "2 hours before":
                return 2 * 60 * 60 * 1000;
            case "12 hours before":
                return 12 * 60 * 60 * 1000;
            case "1 day before":
                return 24 * 60 * 60 * 1000;
            case "1 week before":
                return 7 * 24 * 60 * 60 * 1000;
            default:
                return -1;
        }
    }

    private void scheduleReminder(String taskName, Calendar reminderTime) {
        Intent intent = new Intent(requireContext(), ReminderReceiver.class);
        intent.putExtra("taskName", taskName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.getTimeInMillis(), pendingIntent);
        }
    }
}

