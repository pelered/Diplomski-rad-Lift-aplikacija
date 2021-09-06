package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lift11.Model.Lift;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Calibrate extends AppCompatActivity {

    private Button start;
    private Button save;
    private Button reset;
    private Button startTracker;

    private TextView maxAccText;
    private TextView minAccText;
    private TextView activeAcc;

    private Accelerometer accelerometer;

    private int counter = 0;

    //acceleration
    private float maxAcceleration = 0;
    private float minAcceleration = 0;
    private boolean onAcc = false;


    private float sum = 0;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Lift lift;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);

        start = (Button) findViewById(R.id.startButton);
        save = (Button) findViewById(R.id.saveButton);
        reset = findViewById(R.id.buttonReset);
        startTracker = findViewById(R.id.start_lift_tracker);

        maxAccText = (TextView) findViewById(R.id.maxAccText);
        minAccText = (TextView) findViewById(R.id.minAccText);
        activeAcc = findViewById(R.id.textActiveAcc);

        accelerometer = new Accelerometer(this);
        onPause();

        ///-------------------------------------
        prefs = Objects.requireNonNull(this).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

        myRef= FirebaseDatabase.getInstance().getReference("Liftovi");


        myRef.orderByKey().equalTo("u_uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                    lift = recipeSnapshot.getValue(Lift.class);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });




        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tz) {
                if (onAcc) {
                    calibrateAcceleration(tz);
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onResume();
                onAcc = true;
                minAcceleration = 0;
                maxAcceleration = 0;
                activeAcc.setText("Active");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcc = false;
                activeAcc.setText("Not active");
                try {

                    SharedPreferences.Editor editor = prefs.edit();

                    if (maxAcceleration > 0 && minAcceleration < 0){
                        editor.putFloat("MAX_ACC_KEY", maxAcceleration);
                        editor.putFloat("MIN_ACC_KEY", minAcceleration);
                        editor.commit();
                        Toast.makeText(Calibrate.this, "Acceleration information saved", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(Calibrate.this, "No acceleration information", Toast.LENGTH_SHORT).show();
                    }

                    counter = 10;
                    onPause();
                }catch (Exception e){
                    Toast.makeText(Calibrate.this, "No acceleration information", Toast.LENGTH_SHORT).show();
                }

                izmjereno();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAcc = false;
                activeAcc.setText("Not Active");
                onPause();
                try {
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putFloat("MAX_ACC_KEY", 0);
                    editor.putFloat("MIN_ACC_KEY", 0);
                    editor.commit();
                    Toast.makeText(Calibrate.this, "Acceleration data reset", Toast.LENGTH_SHORT).show();

                    maxAccText.setText(0+"");
                    minAccText.setText(0+"");
                    maxAcceleration = 0;
                    minAcceleration = 0;

                }catch (Exception e){
                    Toast.makeText(Calibrate.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        startTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxAcceleration != 0 && minAcceleration != 0){

                    Intent intent = new Intent(Calibrate.this, Mjerenje.class);
                    startActivity(intent);
                }
            }
        });


    }

    private void izmjereno() {
        lift.setMax_ac(maxAcceleration);
        lift.setMin_ac(minAcceleration);
    }

    private void spremi(){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("u_uid", lift);
        myRef.updateChildren(childUpdates);
    }

    private void calibrateAcceleration(float tz) {
        if (counter == 0) {
            sum += tz;
            if (maxAcceleration < sum) {
                maxAcceleration = sum;
                maxAccText.setText(sum+"");
            }
            if (minAcceleration > sum) {
                minAcceleration = sum;
                minAccText.setText(sum+"");
            }
        } else {
            counter--;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
    }
}