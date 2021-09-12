package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lift11.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private SharedPreferences prefs;
    private EditText email, password;
    private Button login;
    private User user_dohvati;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        prefs = Objects.requireNonNull(this).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        builder = new AlertDialog.Builder(this);
        if (prefs.getString("email", null) != null) {
            //idi na sljedeci activity
            if (prefs.getString("lift_id", null) != null) {
                Intent intent = new Intent(this, Mjerenje.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(this, Odabir.class);
                startActivity(intent);
            }

        } else {
            login = findViewById(R.id.login);
            email = findViewById(R.id.email);
            password = findViewById(R.id.lozinka);
            myRef = database.getInstance().getReference("Users");
            login.setOnClickListener(view -> {
                test();
                if (!(email.getText().toString().trim().equals("")) && !(password.getText().toString().trim().equals(""))) {
                    log();
                }
            });

        }
    }


    public void log() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("log s emailom i pass", "signInWithEmail:success");
                final FirebaseUser user = mAuth.getCurrentUser();
                assert user != null;
                myRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        user_dohvati = snapshot.getValue(User.class);
                        Log.d("log():", user_dohvati.toString());
                        assert user_dohvati != null;
                        start();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(getApplicationContext(), "Neuspjelo logiranje", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.w("log s emailom i pas", "signInWithEmail:failure", task.getException());
                Toast.makeText(getApplicationContext(), "Ne postoji korisnik.Molim Vas registriraj te se na web stranici.", Toast.LENGTH_SHORT).show();


            }
        });


    }

    private void test() {

    }

    private void start() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("email", user_dohvati.getEmail());
        editor.putString("u_uid", user_dohvati.getUid());
        editor.putBoolean("verified", user_dohvati.getEmailVerified());
        editor.apply();
        Intent intent = new Intent(this, Odabir.class);
        startActivity(intent);

    }

}
