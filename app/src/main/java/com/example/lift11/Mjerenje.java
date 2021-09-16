package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lift11.Model.DIzalo_kretanja;
import com.example.lift11.Model.Dizalo;
import com.example.lift11.Model.Dizalo_stanje;
import com.example.lift11.Model.Stanje;
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
    private DatabaseReference myRefLift,myRef2, myRefVoznja, myRefMjeri;
    private boolean vrti;
    private SharedPreferences prefs;
    private Button start_mj,stop_mj,log_out;
    private Dizalo dizalo;
    private DIzalo_kretanja liftTravels;
    private Dizalo_stanje dizalo_stanje;
    private CountDownTimer mCountDownTimer;
    private String lift_key;
    private int p_k;//pocetni kat
    private int z_k;//zavrsni kat
    private int n_k;//najnizi kat
    private int v_k;//najvisi kat
    float batteryPct;
    private Stanje stanje_mjeri_li;
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

        stanje_mjeri_li =new Stanje();
        myRefLift =database.getInstance().getReference("Liftovi");
        myRefMjeri =database.getInstance().getReference("Mjeri");
        myRefLift.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                dizalo =snapshot.getValue(Dizalo.class);
                dizalo.setKey(snapshot.getKey());
                lift_naziv.setText(dizalo.getIme());
                n_k= dizalo.getN_k();
                v_k= dizalo.getV_k();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        myRefMjeri.child(lift_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()==null){

                    stanje_mjeri_li.setU_uid(prefs.getString("u_uid", null));
                    if(vrti){
                        stanje_mjeri_li.setState(true);
                    }else{
                        stanje_mjeri_li.setState(false);
                    }
                    Map<String, Object> up = new HashMap<>();
                    up.put(lift_key, stanje_mjeri_li);

                    myRefMjeri.updateChildren(up);
                }else{



                    stanje_mjeri_li =snapshot.getValue(Stanje.class);
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        myRefMjeri.child(lift_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null){
                    if(!Objects.requireNonNull(snapshot.getValue(Stanje.class)).getState()){
                        vrti=false;
                        stanje_mjeri_li.setState(false);
                        Map<String, Object> up = new HashMap<>();
                        up.put(lift_key, stanje_mjeri_li);
                        myRefMjeri.updateChildren(up);
                        mjeri.setText("Zaustavljeno mjerenje");
                    }else if(Objects.requireNonNull(snapshot.getValue(Stanje.class)).getState()){
                        vrti=true;
                        mjeri.setText("Mjeri");
                        stanje_mjeri_li.setState(true);
                        Map<String, Object> up = new HashMap<>();
                        up.put(lift_key, stanje_mjeri_li);
                        myRefMjeri.updateChildren(up);

                        postavi_vrijednosti();
                    }
                }

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
            stanje_mjeri_li.setState(true);
            Map<String, Object> up = new HashMap<>();
            up.put(lift_key, stanje_mjeri_li);
            myRefMjeri.updateChildren(up);

            postavi_vrijednosti();

        }
        else if(view.equals(stop_mj)){
            vrti=false;
            stanje_mjeri_li.setState(false);
            Map<String, Object> up = new HashMap<>();
            up.put(lift_key, stanje_mjeri_li);
            myRefMjeri.updateChildren(up);
            mjeri.setText("Zaustavljeno mjerenje");

        }
        else if(view.equals(log_out)){
            //ovdje
            if(vrti){
                Toast.makeText(this,"Morate prvo zaustaviti mjerenje da biste se mogli odjaviti",Toast.LENGTH_SHORT).show();
            }else{
                builder.setMessage("Želite li odspojiti senzor od lifta i zaustaviti mjerenje?")
                        .setCancelable(false)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AuthUI.getInstance()
                                        .signOut(getApplicationContext())
                                        .addOnCompleteListener(task -> {
                                            vrti=false;
                                            stanje_mjeri_li.setState(false);
                                            Map<String, Object> up = new HashMap<>();
                                            up.put(lift_key, stanje_mjeri_li);
                                            myRefMjeri.updateChildren(up);

                                            dizalo.setIs_connected(false);

                                            Map<String, Object> update1 = new HashMap<>();
                                            update1.put("is_connected", false);
                                            // user is now signed out
                                            SharedPreferences prefs = getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = prefs.edit();
                                            editor.clear();
                                            editor.apply();



                                            myRefLift.child(lift_key).updateChildren(update1).addOnCompleteListener(task1 -> {

                                                if (task.isSuccessful()) {
                                                    startActivity(new Intent(Mjerenje.this, Prijava.class));
                                                    finish();}
                                                if(task.isCanceled()){
                                                    Log.d("Neuspjeh",task.getResult().toString());
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
    private void start_timer() {

        int count_p = getRandomNumber(1, 6);
        myRef2=database.getInstance().getReference("Stanje");
        String start_time = Calendar.getInstance().getTime().toString();
        final int[] p_k_t = {p_k};

        mCountDownTimer = new CountDownTimer((z_k-p_k)*1000, 1000) {
            @Override
            public void onTick(long l) {

                dizalo_stanje =new Dizalo_stanje(Integer.toString(p_k_t[0]),Integer.toString(count_p),String.valueOf(batteryPct) + "%","u pokretu");
                p_k_t[0] = p_k_t[0] +1;
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put( dizalo.getKey(), dizalo_stanje);
                myRef2.updateChildren(childUpdates);
                //ujedno salje info na webstranicu ako gledas taj liftTravels

            }
            @Override
            public void onFinish() {
                String end_time = Calendar.getInstance().getTime().toString();
                liftTravels =new DIzalo_kretanja(p_k,z_k,n_k,v_k,start_time,end_time,count_p, dizalo.getZgrada(), dizalo.getPod_zg(), dizalo.getKey());
                //System.out.println("TEST: "+liftTravels.toString());

                dizalo_stanje =new Dizalo_stanje(Integer.toString(z_k),Integer.toString(count_p),String.valueOf(batteryPct) + "%","miruje");
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put( dizalo.getKey(), dizalo_stanje);
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

        p_k=getRandomNumber(n_k-1,v_k);
        z_k=getRandomNumber(p_k,v_k);

        while(p_k==z_k){

            z_k=getRandomNumber(p_k,(v_k+1));
        }
        start_timer();

    }

    private void save_data_travels() {
        myRefVoznja =database.getInstance().getReference("Putovanja");
        String key= myRefVoznja.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, liftTravels);
        myRefVoznja.updateChildren(childUpdates);
    }
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    //cloud mesaging
    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}