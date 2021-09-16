package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.lift11.Adapter.IspisOldAdapter;
import com.example.lift11.Model.Dizalo;
import com.example.lift11.Model.Zgrada;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class DodajPostojeci extends AppCompatActivity {
    private DatabaseReference myRefLift, myRefZg, myRefPodzg;
    private FirebaseDatabase database;
    private SharedPreferences prefs;
    private ArrayList<Dizalo> dizala;
    private IspisOldAdapter myadapter;
    private RecyclerView recyclerViewHome;
    private LinearLayoutManager layoutManager;
    private CountDownTimer mCountDownTimer;
    private ProgressBar simpleProgressBar;
    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_postojeci);
        myRefLift =database.getInstance().getReference("Liftovi");
        myRefZg =database.getInstance().getReference("Projekti/Zgrade");
        myRefPodzg =database.getInstance().getReference("Projekti/Podzgrade");


        prefs = Objects.requireNonNull(this).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

        id_user=prefs.getString("u_uid",null);
        //
        simpleProgressBar=findViewById(R.id.indeterminateBar);
        simpleProgressBar.setVisibility(VISIBLE);
        recyclerViewHome =findViewById(R.id.old_lift_recycle);
        recyclerViewHome.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        layoutManager.scrollToPosition(0);
        recyclerViewHome.setLayoutManager(layoutManager);
        recyclerViewHome.scrollToPosition(0);
        //

        dizala =new ArrayList<>();
        final boolean[] cekaj = {false};
        Log.d("Userje:",id_user);

        myRefLift.orderByChild("u_uid").equalTo(id_user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                    if(recipeSnapshot.getValue(Dizalo.class).getIs_connected()!=null) {
                        if (!recipeSnapshot.getValue(Dizalo.class).getIs_connected()) {
                            dizala.add(recipeSnapshot.getValue(Dizalo.class));
                            dizala.get(dizala.size()-1).setKey(recipeSnapshot.getKey());


                        }
                    }
                }

                dohvatiZg();


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });



    }

    private void dohvatiZg() {
        for(int i = 0; i< dizala.size(); i++){
            int finalI = i;
            myRefZg.orderByKey().equalTo(dizala.get(i).getZgrada()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    for(DataSnapshot recipe1: snapshot.getChildren()) {
                        String ime_p=recipe1.getValue(Zgrada.class).getIme();
                        dizala.get(finalI).setZg_ime(ime_p);


                    }
                }
                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });

        }
        setPod();
    }

    private void setPod(){
        for(int i = 0; i< dizala.size(); i++){
            if(dizala.get(i).getPod_zg()!=null){
                int finalI1 = i;
                myRefPodzg.orderByKey().equalTo(dizala.get(i).getPod_zg()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot recipe1: snapshot.getChildren()) {
                            String ime_p=recipe1.getValue(Zgrada.class).getIme();
                            dizala.get(finalI1).setPod_ime(ime_p);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }


        }

        mCountDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                setAdapter();

            }
        };
        mCountDownTimer.start();
    }
    private void setAdapter() {
        simpleProgressBar.setVisibility(GONE);
        layoutManager.setReverseLayout(true);
        layoutManager.scrollToPosition(0);
        layoutManager.setStackFromEnd(true);
        recyclerViewHome.scrollToPosition(0);

        myadapter= new IspisOldAdapter(getApplicationContext(), dizala);
        recyclerViewHome.setAdapter(myadapter);
    }
}