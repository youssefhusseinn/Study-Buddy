package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class NewTaskActivity extends AppCompatActivity {

    EditText titleEditText, descEditText, dateEditText;
    Button saveTaskButton, cancelTaskButton;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Log.i("Test","NewTasksActivity.");
        mAuth = FirebaseAuth.getInstance();

        titleEditText = findViewById(R.id.titleEditText);
        descEditText = findViewById(R.id.descEditText);
        dateEditText = findViewById(R.id.dateEditText);

        saveTaskButton = findViewById(R.id.saveTaskButton);
        cancelTaskButton = findViewById(R.id.cancelTaskButton);

        saveTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String titleTask = titleEditText.getText().toString();
                final String dateTask = dateEditText.getText().toString();
                final String descTask = descEditText.getText().toString();
                final String keyTask = String.valueOf(new Random().nextInt());

                insertIntoSQLite(titleTask, dateTask, descTask, keyTask);

                if(!MainActivity.guest) {
                    new UpdateFirebase().execute(retrieveFromDatabase());
                    redirect();
                    //insert data to database
                    /*
                    reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Tasks").child("Task" + keyTask);
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("titleTask").setValue(titleTask);
                            snapshot.getRef().child("descTask").setValue(descTask);
                            snapshot.getRef().child("dateTask").setValue(dateTask);
                            snapshot.getRef().child("keyTask").setValue(keyTask);

                            redirect();

                            //finish();
                            //startActivity(new Intent(NewTaskActivity.this, TasksActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Test",error.getDetails());
                            redirect();
                        }
                    });
                    */
                }else{
                    redirect();
                    finish();
                }
            }
        });

        cancelTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirect();
            }
        });
    }

    private void insertIntoSQLite(final String titleTask, final String dateTask, final String descTask, final String keyTask) {
        try {
            MainActivity.db.execSQL("INSERT INTO tasks (titleTask, dateTask, descTask, keyTask) VALUES (?,?,?,?)", new String[]{titleTask, dateTask, descTask, keyTask});
        }catch (Exception e){
            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show();
            Log.i("Test", e.getMessage());
        }
    }

    private void redirect(){
        Intent intent = new Intent(NewTaskActivity.this, TasksActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        //finish();
    }

    public class UpdateFirebase extends AsyncTask<ArrayList<MyTask>, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Tasks");
            reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.i("Test","Deleted all tasks for this user");
                    }else{
                        Log.i("Test","Error occurred while deleting tasks (New Tasks Activity)");
                    }
                }
            });
        }

        @Override
        protected Void doInBackground(ArrayList<MyTask>... arrayLists) {
            ArrayList<MyTask> list = arrayLists[0];
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("Tasks");
            for(MyTask task : list){
                final String titleTask = task.getTitleTask();
                final String dateTask = task.getDateTask();
                final String descTask = task.getDescTask();
                final String keyTask = task.getKeyTask();
                reference.child("Task"+keyTask).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        snapshot.getRef().child("titleTask").setValue(titleTask);
                        snapshot.getRef().child("dateTask").setValue(dateTask);
                        snapshot.getRef().child("descTask").setValue(descTask);
                        snapshot.getRef().child("keyTask").setValue(keyTask);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(NewTaskActivity.this,"Error occurred", Toast.LENGTH_SHORT).show();
                        Log.i("ERROR", error.getDetails());
                        Log.i("ERROR", error.getMessage());
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            finish();
        }
    }
    private ArrayList<MyTask> retrieveFromDatabase(){
        ArrayList<MyTask> list = new ArrayList<>();
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
        return list;
    }
}