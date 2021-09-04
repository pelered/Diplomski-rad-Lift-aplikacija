package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
import android.widget.TextView;

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
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Mjerenje extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase database;
    private DatabaseReference myRef,myRef2,myRef3,myRef4;
    private boolean vrti;
    private SharedPreferences prefs;
    private Button start_mj,stop_mj,log_out;
    private Lift lift;
    private Lift_travels liftTravels;
    private Lift_state lift_state;
    private CountDownTimer mCountDownTimer;
    private String lift_key;
    private int p_k;//pocetni kat
    private int z_k;//zavrsni kat
    private int n_k;//najnizi kat
    private int v_k;//najvisi kat
    float batteryPct;
    private State state_mjeri_li;
    private AlertDialog.Builder builder;
    private String id_user;
    private TextView zgrada,podzg,lift_naziv,mjeri;
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            batteryPct = level * 100 / (float)scale;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mjerenje);
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        lift_key=prefs.getString("lift_id",null);
        vrti=false;
        start_mj=findViewById(R.id.start_mj);
        stop_mj=findViewById(R.id.stop_mj);
        log_out=findViewById(R.id.log_out);
        zgrada=findViewById(R.id.naziv_zgrade);
        podzg=findViewById(R.id.naziv_podzgrade);
        lift_naziv=findViewById(R.id.naziv_lista);
        mjeri=findViewById(R.id.stanje_mj);
        builder = new AlertDialog.Builder(this);
        log_out.setOnClickListener(this);
        start_mj.setOnClickListener(this);

        stop_mj.setOnClickListener(this);
        if (!vrti){
            mjeri.setText("Zaustavljeno mjerenje");
        }

        state_mjeri_li =new State();
        myRef=database.getInstance().getReference("Liftovi");
        myRef3=database.getInstance().getReference("Stanje");
        myRef4=database.getInstance().getReference("Mjeri");
        myRef4.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("Mjeri",snapshot.toString());
                if(snapshot.getValue()==null){

                    state_mjeri_li.setU_uid(prefs.getString("u_uid", null));
                    if(vrti){
                        state_mjeri_li.setState(true);
                    }else{
                        state_mjeri_li.setState(false);
                    }
                }else{
                        state_mjeri_li =snapshot.getValue(State.class);
                        state_mjeri_li.setKey(snapshot.getKey());


                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("tag1", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    // Log and toast
                    if(state_mjeri_li != null ){
                        if(state_mjeri_li.getFcmTokens()!=null){
                            if(!state_mjeri_li.getFcmTokens().equals(token)){
                                state_mjeri_li.setFcmTokens(token);
                            }
                        }else{
                            state_mjeri_li.setFcmTokens(token);
                        }


                    }else{
                        state_mjeri_li =new State();
                        state_mjeri_li.setU_uid(prefs.getString("u_uid", null));
                        if(vrti){
                            state_mjeri_li.setState(true);
                        }else{
                            state_mjeri_li.setState(false);
                        }
                        state_mjeri_li.setFcmTokens(token);
                    }
                    Log.d("tag", token);
                    //Toast.makeText(this, token, Toast.LENGTH_SHORT).show();
                });
        myRef.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                lift=snapshot.getValue(Lift.class);
                lift.setKey(snapshot.getKey());
                lift_naziv.setText(lift.getIme());
                n_k=lift.getN_k();
                v_k=lift.getV_k();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if(view.equals(start_mj)){
            vrti=true;
            mjeri.setText("Mjeri");
            state_mjeri_li.setState(true);
            Map<String, Object> up = new HashMap<>();
            up.put(lift_key, state_mjeri_li);
            myRef4.updateChildren(up);


            postavi_vrijednosti();

        }
        else if(view.equals(stop_mj)){
            vrti=false;
            mjeri.setText("Mjeri");
            state_mjeri_li.setState(false);
            Map<String, Object> up = new HashMap<>();
            up.put(lift_key, state_mjeri_li);
            myRef4.updateChildren(up);
            mjeri.setText("Zaustavljeno mjerenje");

        }
        else if(view.equals(log_out)){
            //ovdje
            builder.setMessage("Želite li odspojiti senzor od lifta i zaustiviti mjerenje?")
                    .setCancelable(false)
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AuthUI.getInstance()
                                    .signOut(getApplicationContext())
                                    .addOnCompleteListener(task -> {
                                        vrti=false;
                                        state_mjeri_li.setState(false);
                                        Map<String, Object> up = new HashMap<>();
                                        up.put(lift_key, state_mjeri_li);
                                        myRef4.updateChildren(up);
                                        //Log.d("Key",lift_key);
                                        Map<String, Object> update = new HashMap<>();
                                        update.put("is_connected", false);
                                        myRef.child(lift_key).updateChildren(update).addOnCompleteListener(task1 -> {
                                            if (task.isSuccessful()) {Log.d("Uspjeh","d");}

                                        });
                                        // user is now signed out
                                        SharedPreferences prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.clear();
                                        editor.apply();
                                        startActivity(new Intent(Mjerenje.this, Login.class));
                                        finish();
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
    private void start_timer() {

        int count_p = getRandomNumber(1, 6);
        myRef2=database.getInstance().getReference("Stanje");
        String start_time = Calendar.getInstance().getTime().toString();
        final int[] p_k_t = {p_k};
        mCountDownTimer = new CountDownTimer((z_k-p_k)*1000, 1000) {
            @Override
            public void onTick(long l) {

                Log.d("Tik:", String.valueOf(p_k_t[0])+", "+String.valueOf(l)+","+String.valueOf(z_k));
                lift_state=new Lift_state(Integer.toString(p_k_t[0]),Integer.toString(count_p),String.valueOf(batteryPct) + "%","u pokretu");
                p_k_t[0] = p_k_t[0] +1;
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put( lift.getKey(), lift_state);
                myRef2.updateChildren(childUpdates);
                //ujedno salje info na webstranicu ako gledas taj liftTravels

            }
            @Override
            public void onFinish() {
                String end_time = Calendar.getInstance().getTime().toString();
                liftTravels =new Lift_travels(p_k,z_k,n_k,v_k,start_time,end_time,count_p,lift.getZgrada(),lift.getPod_zg(),lift.getKey());
                //System.out.println("TEST: "+liftTravels.toString());

                lift_state=new Lift_state(Integer.toString(z_k),Integer.toString(count_p),String.valueOf(batteryPct) + "%","miruje");
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put( lift.getKey(), lift_state);
                //System.out.println("TEST6 : "+childUpdates.toString());
                myRef2.updateChildren(childUpdates);
                save_data_travels();
                if(vrti){
                    postavi_vrijednosti();
                }
                //pokreni ponovo ako nije pritisnut stop gumb

            }
        }.start();


    }
    private void postavi_vrijednosti(){
        p_k=getRandomNumber(n_k,v_k);
        z_k=getRandomNumber(p_k,v_k);

        while(p_k==z_k){
            z_k=getRandomNumber(p_k,(v_k+1));
        }
        start_timer();

    }

    private void save_data_travels() {
        myRef=database.getInstance().getReference("Putovanja");
        String key=myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, liftTravels);
        myRef.updateChildren(childUpdates);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    //cloud mesaging
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        Log.d("Pointer:", String.valueOf(hasCapture));

    }
}