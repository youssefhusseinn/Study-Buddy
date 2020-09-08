package com.example.studybuddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class TasksActivity extends AppCompatActivity {

    static TextView titlePage, subTitlePage, endPage;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    RecyclerView ourTasks;
    ArrayList<MyTask> list;
    TaskAdapter taskAdapter;
    static LottieAnimationView relaxAnim;
    static LottieAnimationView checkAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Log.i("Test","TasksActivity.");
        final String username = getIntent().getStringExtra("username");
        //Toast.makeText(this,"Time to get things done, "+username+"!", Toast.LENGTH_SHORT).show();
        titlePage = findViewById(R.id.titlePage);
        subTitlePage = findViewById(R.id.subTitlePage);
        endPage = findViewById(R.id.endPage);
        relaxAnim = findViewById(R.id.relaxAnim);
        checkAnim = findViewById(R.id.checkAnim);
        checkAnim.setVisibility(View.INVISIBLE);

        //work with data
        list = new ArrayList<>();
        ourTasks = findViewById(R.id.ourTasks);
        ourTasks.setLayoutManager(new LinearLayoutManager(this));

        taskAdapter = new TaskAdapter(TasksActivity.this, list);
        ourTasks.setAdapter(taskAdapter);

        //retrieve data from firebase
        list.clear();
        retrieveFromDatabase();
        if(!MainActivity.guest) {
            updateDisplay();
            taskAdapter.notifyDataSetChanged();
            mAuth = FirebaseAuth.getInstance();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("Tasks");
            /*reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //retrieve tasks
                    list.clear();
                    if (snapshot.getValue() == null) {
                        displayEmpty();
                    } else {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            list.add(dataSnapshot.getValue(MyTask.class));
                        }
                        displayNotEmpty();
                    }
                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //show error
                    Toast.makeText(getApplicationContext(), "No Tasks :(", Toast.LENGTH_SHORT).show();
                }
            });*/
        }else{
            updateDisplay();
            taskAdapter.notifyDataSetChanged();
        }
    }

    private void updateDisplay(){
        if(list.isEmpty()){
            displayEmpty();
        }else{
            displayNotEmpty();
        }
    }

    public static void displayEmpty(){
        endPage.setText("You're task-free! Time to relax.");
        relaxAnim.playAnimation();
        relaxAnim.setVisibility(View.VISIBLE);
    }
    public static void displayNotEmpty(){
        endPage.setText("End of tasks!");
        relaxAnim.setVisibility(View.INVISIBLE);
        relaxAnim.pauseAnimation();
    }
    public static void taskFinished(){

    }
    public void addTask(View view){
        startActivity(new Intent(TasksActivity.this, NewTaskActivity.class));
    }
    public void goToTimer(View view){
        startActivity(new Intent(TasksActivity.this, TimerActivity.class));
    }
    public void logout(View view){
        if(!MainActivity.guest && mAuth.getCurrentUser() != null)
            mAuth.signOut();
        Intent intent = new Intent(TasksActivity.this, MainActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void retrieveFromDatabase(){
        Cursor c = MainActivity.db.rawQuery("SELECT * FROM tasks", null);

        int titleTaskIndex = c.getColumnIndex("titleTask");
        int dateTaskIndex = c.getColumnIndex("dateTask");
        int descTaskIndex = c.getColumnIndex("descTask");
        int keyTaskIndex = c.getColumnIndex("keyTask");

        c.moveToFirst();

        while(!c.isAfterLast()){
            list.add(new MyTask(c.getString(titleTaskIndex), c.getString(dateTaskIndex), c.getString(descTaskIndex), c.getString(keyTaskIndex)));
            c.moveToNext();
        }
        c.close();
    }
}