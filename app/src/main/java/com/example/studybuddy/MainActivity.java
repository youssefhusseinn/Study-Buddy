package com.example.studybuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText emailEditText, passwordEditText;
    public static boolean guest = false;
    public static SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("Test","MainActivity.");
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        db = this.openOrCreateDatabase("Tasks", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tasks (titleTask VARCHAR, dateTask VARCHAR, descTask VARCHAR, keyTask VARCHAR)");

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {

            //TODO RETRIEVE USERNAME
            //login("Test");
        }
    }

    public void loginClicked(View view){
        guest = false;
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(MainActivity.this, "All fields required!",Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //TODO: retrieve username from database
                    login("Test");
                }else{
                    passwordEditText.setText("");
                    String error = "";
                    if(task.getException() != null)
                        error = task.getException().toString().substring(task.getException().toString().indexOf(" "));
                    else
                        error = "Invalid email or password!";
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void registerClicked(View view){
        guest = false;
        final String email = emailEditText.getText().toString();
        final String password = passwordEditText.getText().toString();
        if(email.equals("") || password.equals("")){
            Toast.makeText(MainActivity.this, "All fields required!",Toast.LENGTH_SHORT).show();
            return;
        }
        final EditText usernameEditText = new EditText(this);
        usernameEditText.setHint("Username");
        new AlertDialog.Builder(this)
                .setTitle("Type Username")
                .setView(usernameEditText)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String username = usernameEditText.getText().toString();
                        if(username.length()==0)
                            return;
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid());
                                    reference.child("email").setValue(email);
                                    reference.child("username").setValue(username);
                                    reference.child("password").setValue(password);
                                    Log.i("MainActivity","Sign up successful");
                                    login(username);
                                }else{
                                    String error = "";
                                    if(task.getException() != null)
                                        error = task.getException().toString().substring(task.getException().toString().indexOf(" "));
                                    else
                                        error = "Error occurred!";
                                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel",null)
                .show();
    }
    public void guestClicked(View view){
        //TODO START SQLite PART
        guest = true;
/*
        final EditText usernameEditText = new EditText(this);
        usernameEditText.setHint("Username");
        new AlertDialog.Builder(this)
                .setTitle("Type Username")
                .setView(usernameEditText)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String username = usernameEditText.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("users").child(username+"Guest").child("guest").setValue("true");
                        loginAsGuest(username);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
*/

        login("-");
    }

    public void login(String username){
        Intent intent = new Intent(MainActivity.this, TasksActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }
}