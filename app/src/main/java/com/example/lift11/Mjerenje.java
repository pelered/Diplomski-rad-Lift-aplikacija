package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lift11.Model.Lift;
import com.example.lift11.Model.Lift_state;
import com.example.lift11.Model.Lift_travels;
import com.example.lift11.Model.State;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mjerenje extends AppCompatActivity implements View.OnClickListener, LifecycleObserver {
    private FirebaseDatabase database;
    private DatabaseReference myRefLift, myRef2, myRefVoznja, myRefMjeri;
    private SharedPreferences prefs;

    private Accelerometer accelerometer;
    private Movement movement;

    private State state_mjeri_li;

    private AlertDialog.Builder builder;

    float batteryPct;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPct = level * 100 / (float) scale;
        }
    };

    private TextView zgrada, podzg, lift_naziv, mjeri, trenuti_kat, status_kretanja;
    private EditText startFloorEditText;
    private Button start_mj, stop_mj, log_out, show_graph;

    private Lift lift;
    private Lift_travels liftTravels;
    private Lift_state lift_state;
    private String lift_key;
    private String id_user;

    private boolean vrti = false;
    private boolean tracking = false;
    private int p_k;//pocetni kat startFloor
    private int z_k;//zavrsni kat
    private int n_k;//najnizi kat startFloor
    private int v_k;//najvisi kat nbOfFloors
    private float maxAcc;
    private float minAcc;
    private String startTime;
    private String endTime;
    private int currentFloor;
    private int previousFloor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mjerenje);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        accelerometer = new Accelerometer(this);
        movement = new Movement();

        prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        lift_key = prefs.getString("lift_id", null);
        vrti = false;

        start_mj = findViewById(R.id.start_mj);
        stop_mj = findViewById(R.id.stop_mj);
        log_out = findViewById(R.id.log_out);
        zgrada = findViewById(R.id.naziv_zgrade);
        podzg = findViewById(R.id.naziv_podzgrade);
        lift_naziv = findViewById(R.id.naziv_lista);
        mjeri = findViewById(R.id.stanje_mj);
        trenuti_kat = findViewById(R.id.current_floor);
        status_kretanja = findViewById(R.id.up_down_status);
        startFloorEditText = findViewById(R.id.startNb);

        builder = new AlertDialog.Builder(this);

        log_out.setOnClickListener(this);
        start_mj.setOnClickListener(this);
        stop_mj.setOnClickListener(this);

        accelerometer.setListener(new Accelerometer.Listener() {
            @Override
            public void onTranslation(float tz) {
                if (tracking) {
                    movement.Prati(tz);
                    updateVariables();
                    podzg.setText(tz + "");
                }
            }
        });

        state_mjeri_li = new State();

        myRefLift = database.getInstance().getReference("Liftovi");
        myRefMjeri = database.getInstance().getReference("Mjeri");

        myRefMjeri.child(lift_key);

        myRefMjeri.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                //Log.d("Mjeri",snapshot.toString());
                if (snapshot.getValue() == null) {
                    state_mjeri_li.setU_uid(prefs.getString("u_uid", null));
                    if (vrti) {
                        state_mjeri_li.setState(true);
                        Toast.makeText(Mjerenje.this, "true"  , Toast.LENGTH_LONG).show();
                    } else {
                        state_mjeri_li.setState(false);
                        Toast.makeText(Mjerenje.this, "false"  , Toast.LENGTH_LONG).show();
                    }
                    Map<String, Object> up = new HashMap<>();
                    up.put(lift_key, state_mjeri_li);
                    myRefMjeri.updateChildren(up);
                } else {
                    state_mjeri_li = snapshot.getValue(State.class);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        myRefMjeri.child(lift_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("Data", String.valueOf(snapshot));
                if (snapshot.getValue() != null) {
                    if (!Objects.requireNonNull(snapshot.getValue(State.class)).getState()) {
                        vrti = false;
                        state_mjeri_li.setState(false);
                        Map<String, Object> up = new HashMap<>();
                        up.put(lift_key, state_mjeri_li);
                        myRefMjeri.updateChildren(up);
                        mjeri.setText("Zaustavljeno mjerenje");
                        endTracking();
                    } else if (Objects.requireNonNull(snapshot.getValue(State.class)).getState()) {
                        vrti = true;
                        mjeri.setText("Mjeri");
                        state_mjeri_li.setState(true);
                        Map<String, Object> up = new HashMap<>();
                        up.put(lift_key, state_mjeri_li);
                        myRefMjeri.updateChildren(up);
                        startTracking();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        myRefLift.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                lift = snapshot.getValue(Lift.class);
                lift.setKey(snapshot.getKey());
                lift_naziv.setText(lift.getIme());
                zgrada.setText(lift.getZg_ime());
                n_k = lift.getN_k();
                v_k = lift.getV_k();
                maxAcc = lift.getMax_ac();
                minAcc = lift.getMin_ac();
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.equals(start_mj)) {
            startTracking();

        } else if (view.equals(stop_mj)) {
            endTracking();

        } else if (view.equals(log_out)) {
            //ovdje
            if (vrti) {
                Toast.makeText(this, "Morate prvo zaustaviti mjerenje da biste se mogli odjaviti", Toast.LENGTH_SHORT).show();
            } else {
                builder.setMessage("Želite li odspojiti senzor od lifta i zaustaviti mjerenje?")
                        .setCancelable(false)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(task -> {
                                            vrti = false;
                                            state_mjeri_li.setState(false);
                                            Map<String, Object> up = new HashMap<>();
                                            up.put(lift_key, state_mjeri_li);
                                            myRefMjeri.updateChildren(up);
                                            Log.d("Key", lift_key);
                                            Log.d("State", state_mjeri_li.toString());
                                            lift.setIs_connected(false);

                                            Map<String, Object> update1 = new HashMap<>();
                                            update1.put("is_connected", false);
                                            // user is now signed out
                                            SharedPreferences prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.clear();
                                            editor.apply();
                                            Log.d("Lift2:", lift.toString());
                                            //todo ne stavi false svaki put pri odjavi
                                            Log.d("Lift2.2:", myRefLift.toString());


                                            myRefLift.child(lift_key).updateChildren(update1).addOnCompleteListener(task1 -> {

                                                if (task.isSuccessful()) {
                                                    Log.d("Uspjeh", "d");
                                                    startActivity(new Intent(Mjerenje.this, Login.class));
                                                    finish();
                                                }
                                                if (task.isCanceled()) {
                                                    Log.d("Neuspjeh", task.getResult().toString());
                                                }

                                            });


                                        });

                            }
                        })
                        .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void endTracking() {
        startFloorEditText.setText(currentFloor + "");

        tracking = false;
        vrti = false;
        state_mjeri_li.setState(false);
        Map<String, Object> up = new HashMap<>();
        up.put(lift_key, state_mjeri_li);

        myRefMjeri.updateChildren(up);
        mjeri.setText("Zaustavljeno mjerenje");
        endTime = Calendar.getInstance().getTime().toString();
        save_data_travels();
        resetAll();
    }

    private void startTracking() {
        if (setFirstFloor()) {
            setMovementVariables();
            tracking = true;
            vrti = true;
            mjeri.setText("Mjeri");
            state_mjeri_li.setState(true);
            Map<String, Object> up = new HashMap<>();
            up.put(lift_key, state_mjeri_li);
            myRefMjeri.updateChildren(up);
            startTime = Calendar.getInstance().getTime().toString();
        } else {
            return;

        }
    }

    private void resetAll() {
        movement.reset();
    }

    private boolean setFirstFloor() {
        String k = startFloorEditText.getText().toString();
        p_k = Integer.parseInt(k);
        if (p_k >= n_k && p_k <= v_k) {
            currentFloor = p_k;
            return true;
        } else {
            Toast.makeText(Mjerenje.this, "Početni broj nije točan", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void updateVariables() {
        setStatus();
        trenuti_kat.setText(movement.getCurrentFloor() + "");
        if (previousFloor != movement.getCurrentFloor()) {
            myRef2 = database.getInstance().getReference("Stanje");
            lift_state = new Lift_state(Integer.toString(movement.getCurrentFloor()), Integer.toString(0), String.valueOf(batteryPct) + "%", "u pokretu");
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(lift.getKey(), lift_state);
            myRef2.updateChildren(childUpdates);
            currentFloor = movement.getCurrentFloor();
        }
        previousFloor = movement.getCurrentFloor();
    }

    private void save_data_travels() {

        myRefVoznja = database.getInstance().getReference("Putovanja");
        liftTravels = new Lift_travels(p_k, currentFloor, n_k, v_k, startTime, endTime, 0, "", "", lift_key);
        String key = myRefVoznja.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, liftTravels);
        myRefVoznja.updateChildren(childUpdates);

        myRef2 = database.getInstance().getReference("Stanje");
        lift_state = new Lift_state(Integer.toString(movement.getCurrentFloor()), Integer.toString(0), String.valueOf(batteryPct) + "%", "nije u pokretu");
        Map<String, Object> childUpdates1 = new HashMap<>();
        childUpdates1.put(lift.getKey(), lift_state);
        myRef2.updateChildren(childUpdates1);
        currentFloor = movement.getCurrentFloor();

    }

    private void setMovementVariables() {
        movement.setNbOfFloors(v_k);
        movement.setStartFloor(p_k);
        movement.setMaxAmp(maxAcc);
        movement.setMinAmp(minAcc);
    }

    private void setStatus() {
        switch (movement.getUpDown()) {
            case 0:
                status_kretanja.setText("Stationary");
                break;
            case 1:
                status_kretanja.setText("Going up");
                break;
            case 2:
                status_kretanja.setText("Going down");
                break;
            case 4:
                status_kretanja.setText("Braking");
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d("Pointer:", String.valueOf(hasCapture));
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

    /*
        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            super.onResume();
            prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

            myRefMjeri.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    Log.d("Mjeri", snapshot.toString());
                    if (snapshot.getValue() == null) {
                        state_mjeri_li.setU_uid(prefs.getString("u_uid", null));
                        if (vrti) {
                            state_mjeri_li.setState(true);
                        } else {
                            state_mjeri_li.setState(false);
                        }
                    } else {
                        state_mjeri_li = snapshot.getValue(State.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });


            myRefLift.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    lift = snapshot.getValue(Lift.class);
                    lift.setKey(snapshot.getKey());
                    lift_naziv.setText(lift.getIme());
                    n_k = lift.getN_k();
                    v_k = lift.getV_k();
                    maxAcc = lift.getMax_ac();
                    minAcc = lift.getMin_ac();
                    p_k = n_k;
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    */
    /*
        private void start_timer() {

            int count_p = getRandomNumber(1, 6);
            myRef2 = database.getInstance().getReference("Stanje");
            String start_time = Calendar.getInstance().getTime().toString();
            final int[] p_k_t = {p_k};

            mCountDownTimer = new CountDownTimer((z_k - p_k) * 1000, 1000) {
                @Override
                public void onTick(long l) {

                    Log.d("Tik:", String.valueOf(p_k_t[0]) + ", " + String.valueOf(l) + "," + String.valueOf(z_k));
                    lift_state = new Lift_state(Integer.toString(p_k_t[0]), Integer.toString(count_p), String.valueOf(batteryPct) + "%", "u pokretu");
                    p_k_t[0] = p_k_t[0] + 1;
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(lift.getKey(), lift_state);
                    myRef2.updateChildren(childUpdates);
                    //ujedno salje info na webstranicu ako gledas taj liftTravels

                }

                @Override
                public void onFinish() {
                    String end_time = Calendar.getInstance().getTime().toString();
                    liftTravels = new Lift_travels(p_k, z_k, n_k, v_k, start_time, end_time, count_p, lift.getZgrada(), lift.getPod_zg(), lift.getKey());
                    //System.out.println("TEST: "+liftTravels.toString());

                    lift_state = new Lift_state(Integer.toString(z_k), Integer.toString(count_p), String.valueOf(batteryPct) + "%", "miruje");
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(lift.getKey(), lift_state);
                    //System.out.println("TEST6 : "+childUpdates.toString());
                    myRef2.updateChildren(childUpdates);
                    save_data_travels();
                    if (vrti) {
                        postavi_vrijednosti();
                    }
                    //pokreni ponovo ako nije pritisnut stop gumb

                }
            }.start();


        }

    private void postavi_vrijednosti() {

        //start_timer();
    }*/
}