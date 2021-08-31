 package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lift11.Model.Lift;
import com.example.lift11.Model.Lift_travels;
import com.example.lift11.Model.Zgrada;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DodajLift extends AppCompatActivity implements View.OnClickListener {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Button dodaj;
    private AutoCompleteTextView naziv_zg,naziv_pod,naziv_lift;
    private EditText n_k,v_k;
    private ArrayList<Zgrada> zg,podzg;
    private ArrayList<Lift> lifts;
    private Zgrada zgrada_obj;
    private Lift_travels liftTravels;
    private Lift lift;
    private SharedPreferences prefs;
    private String odabir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_lift);
        prefs = Objects.requireNonNull(this).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

        dodaj=findViewById(R.id.gumb_dodaj_lift);
        naziv_zg=findViewById(R.id.naziv);
        naziv_pod=findViewById(R.id.podzg);
        naziv_lift=findViewById(R.id.lift);
        n_k=findViewById(R.id.n_k);
        v_k=findViewById(R.id.v_k);
        zg=new ArrayList<>();
        podzg=new ArrayList<>();
        lifts=new ArrayList<>();

        prefs = Objects.requireNonNull(this).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);

        dohvati_podatke();
        dodaj.setOnClickListener(this);
        naziv_zg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean pro=false;
                for(i=0;i<zg.size();i++){
                    if(zg.get(i).getIme().equals(naziv_zg.getText().toString())){
                        if(zg.get(i).getPodzg()==null && zg.get(i).getLifts()!=null){
                            naziv_pod.setEnabled(false);
                            pro=true;
                        }
                    }
                }
                if(pro==false){
                    naziv_pod.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void dohvati_podatke(){
        final ArrayAdapter<String> zgradaArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        final ArrayAdapter<String> podzgArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);

        myRef=database.getInstance().getReference("Projekti/Zgrade");
        String user=prefs.getString("u_uid",null);
        myRef.orderByChild("u_uid").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot recipeSnapshot: snapshot.getChildren()){
                    zg.add(recipeSnapshot.getValue(Zgrada.class));
                    zg.get(zg.size()-1).setKey(recipeSnapshot.getKey());
                    zgradaArrayAdapter.add(recipeSnapshot.getValue(Zgrada.class).getIme());
                }
                naziv_zg.setAdapter(zgradaArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef= FirebaseDatabase.getInstance().getReference("Projekti/Podzgrade");
        myRef.orderByChild("u_uid").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot recipeSnapshot: snapshot.getChildren()){
                    podzg.add(recipeSnapshot.getValue(Zgrada.class));
                    podzg.get(podzg.size()-1).setKey(recipeSnapshot.getKey());
                    podzgArrayAdapter.add(recipeSnapshot.getValue(Zgrada.class).getIme());
                }
                naziv_pod.setAdapter(podzgArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        myRef= FirebaseDatabase.getInstance().getReference("Liftovi");
        myRef.orderByChild("u_uid").equalTo(user).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot recipeSnapshot: snapshot.getChildren()) {
                    lifts.add(recipeSnapshot.getValue(Lift.class));
                    lifts.get(lifts.size()-1).setKey(recipeSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
    @Override
    public void onClick(View view) {

        if(view.equals(dodaj)){
            if(!naziv_zg.getText().toString().trim().equals("") && !naziv_lift.getText().toString().trim().equals("")
                    &&!n_k.getText().toString().trim().equals("") && !v_k.getText().toString().trim().equals("")){
                //sva polja su popunjena
                if(naziv_pod.getText().toString().trim().equals("")){
                    //nema podzgradu
                    final Boolean[] postoji = {false};
                    lifts.forEach((element)->{
                        //svi liftovi korisnika
                        if(element.getIme().equals(naziv_lift.getText().toString())){
                            //trenutni lift iz liste svih liftova ima isti naziv kao i potencijalni novi lift
                            //ako postoji lift s tim nazivom provjeri dali pripada zgradi s istim nazivom
                            zg.forEach((el_zg)->{


                                //popis svi zgrada korisnika
                                if(el_zg.getLifts().contains(element.getKey()) && el_zg.getIme().equals(naziv_zg.getText().toString())){
                                    //provjeravamo da trenutna zgrada u listi svojih liftova ima lift s novim nazivom
                                    postoji[0] =true;
                                    //ima isti naziv pa stavljamo na true
                                    Toast.makeText(getApplicationContext(),"Lift s tim imenom postoji pod tom zgradom",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    if(!postoji[0]){
                        //dodajemo novi lift

                        //provjera da ne bi dodali postojeci lift
                        //ne postoji radimo update bez dohvacanja key-a,samo lift spremamo
                        for(int i=0; i<zg.size();i++){
                            if(naziv_zg.getText().toString().equals(zg.get(i).getIme())){
                                //dodajemo lift u postojecu zgradu


                               String key_lift;
                                //spremi ako zgrada s tim nazivom nema lift tog naziva;

                                key_lift=save_liftove(zg.get(i).getKey(),naziv_lift.getText().toString(),prefs.getString("u_uid",null));
                                zg.get(i).getLifts().add(key_lift);
                                save_zgrade_update(zg.get(i).getKey(),naziv_zg.getText().toString(),prefs.getString("u_uid",null),zg.get(i).getLifts());

                            }else if(i==zg.size()-1){
                                //stvaramo novu zgradu s novim liftom
                               String key_zg,key_lift;
                                ArrayList lifts=new ArrayList();
                                //spremi ako zgrada s tim nazivom nema lift tog naziva;
                                key_zg=save_zgradu(naziv_zg.getText().toString(),prefs.getString("u_uid",null));
                                key_lift=save_liftove(key_zg,naziv_lift.getText().toString(),prefs.getString("u_uid",null));
                                lifts.add(key_lift);
                                save_zgrade_update(key_zg,naziv_zg.getText().toString(),prefs.getString("u_uid",null),lifts);
                            }
                        }
                    }

                }else{
                    //ima podzgradu
                    final Boolean[] postoji = {false};
                    lifts.forEach((element)->{
                        //svi liftovi korisnika
                        if(element.getIme().equals(naziv_lift.getText().toString())){
                            //trenutni lift iz liste svih liftova ima isti naziv kao i potencijalni novi list
                            //ako postoji lift s tim nazivom provjeri dali pripada zgradi s istim nazivom
                            zg.forEach((el_zg)->{


                                //popis svi zgrada korisnika
                                if(el_zg.getLifts().contains(element.getKey()) && el_zg.getIme().equals(naziv_zg.getText().toString())){
                                    //provjeravamo da trenutna zgrada u listi svojih liftova ima lift s novim nazivom
                                    //sad moramo jos provjeriti da lift ne pripada podzgradi koju smo odabrali
                                    podzg.forEach((el_podzg)->{
                                        if(el_podzg.getLifts().contains(element.getKey()) && el_podzg.getIme().equals(naziv_pod.getText().toString())) {
                                            postoji[0] =true;
                                            //ima isti naziv pa stavljamo na true
                                            Toast.makeText(getApplicationContext(),"Lift s tim imenom postoji pod tom podzgradom",Toast.LENGTH_SHORT).show();

                                        }

                                        });
                                   }
                            });
                        }
                    });
                    if(!postoji[0]){
                        //dodajemo novi lift
                        //provjera da ne bi dodali postojeci lift
                        //ne postoji radimo update bez dohvacanja key-a,samo lift spremamo
                        for(int i=0; i<zg.size();i++){
                            if(naziv_zg.getText().toString().equals(zg.get(i).getIme())){
                                //dodajemo lift u postojecu zgradu
                                final boolean[] pr = {false};
                                int finalI = i;
                                podzg.forEach((el_podzg)->{
                                    if(naziv_pod.getText().toString().equals(el_podzg.getIme())&& zg.get(finalI).getPodzg().contains(el_podzg.getKey())){
                                        pr[0] =true;
                                        //dodajemo lift u postojecu podzgradu
                                        String key_lift;
                                        //spremamo zgradu i dobivamo njezin kljuc ,takoder isto za podzgradu radimo
                                        key_lift=save_liftove(el_podzg.getZg_id(),el_podzg.getKey(),naziv_lift.getText().toString(),prefs.getString("u_uid",null));
                                        el_podzg.getLifts().add(key_lift);
                                        zg.get(finalI).getLifts().add(key_lift);
                                        //azuriramo podzgradu
                                        save_pod_zgradu_update(el_podzg.getKey(),naziv_pod.getText().toString(),prefs.getString("u_uid",null),el_podzg.getLifts(),el_podzg.getZg_id());
                                        //azuriramo zgradu
                                        save_zgrade_update(el_podzg.getZg_id(),naziv_zg.getText().toString(),prefs.getString("u_uid",null),zg.get(finalI).getPodzg(),zg.get(finalI).getLifts());


                                    }
                                });
                                //ako nije nadena podzg s imenom dodajemo novu podzg u postojecu zgradu
                                if(!pr[0]){
                                    String key_lift,key_podzg;
                                    ArrayList lifts=new ArrayList();
                                    ArrayList podzg=new ArrayList();
                                    //spremamo zgradu i dobivamo njezin kljuc ,takoder isto za podzgradu radimo
                                    key_podzg=save_pod_zgradu(naziv_pod.getText().toString(),prefs.getString("u_uid",null),zg.get(i).getKey());
                                    key_lift=save_liftove(zg.get(i).getKey(),key_podzg,naziv_lift.getText().toString(),prefs.getString("u_uid",null));
                                    lifts.add(key_lift);
                                    podzg.add(key_podzg);
                                    //azuriramo podzgradu
                                    save_pod_zgradu_update(key_podzg,naziv_pod.getText().toString(),prefs.getString("u_uid",null),lifts,zg.get(i).getKey());
                                    //azuriramo zgradu
                                    save_zgrade_update(zg.get(i).getKey(),naziv_zg.getText().toString(),prefs.getString("u_uid",null),podzg,lifts);
                                }
                            }else if(i==zg.size()-1){
                                //stvaramo novu zgradu s novim liftom i novom podzgradom
                                String key_zg,key_lift,key_podzg;
                                ArrayList lifts=new ArrayList();
                                ArrayList podzg=new ArrayList();
                                //spremamo zgradu i dobivamo njezin kljuc ,takoder isto za podzgradu radimo
                                key_zg=save_zgradu(naziv_zg.getText().toString(),prefs.getString("u_uid",null));
                                key_podzg=save_pod_zgradu(naziv_pod.getText().toString(),prefs.getString("u_uid",null),key_zg);
                                key_lift=save_liftove(key_zg,key_podzg,naziv_lift.getText().toString(),prefs.getString("u_uid",null));
                                lifts.add(key_lift);
                                podzg.add(key_podzg);
                                //azuriramo podzgradu
                                save_pod_zgradu_update(key_podzg,naziv_pod.getText().toString(),prefs.getString("u_uid",null),lifts,key_zg);
                                //azuriramo zgradu
                                save_zgrade_update(key_zg,naziv_zg.getText().toString(),prefs.getString("u_uid",null),podzg,lifts);
                            }
                        }
                    }




                }
            }

        }
    }
    private void save_zgrade_update(String key,String zgrada,String user,ArrayList<String> pod_zgrada,ArrayList<String> lista_liftova) {
        myRef=database.getInstance().getReference("Projekti/Zgrade");

        zgrada_obj=new Zgrada(zgrada,user,pod_zgrada,lista_liftova);
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put( key, zgrada_obj);
        myRef.updateChildren(childUpdates2).addOnCompleteListener(task -> {

        });

    }
    private void save_zgrade_update(String key,String zgrada,String user,ArrayList<String> lista_liftova) {
       myRef=database.getInstance().getReference("Projekti/Zgrade");
        zgrada_obj=new Zgrada(zgrada,user,lista_liftova);
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put( key, zgrada_obj);

        myRef.updateChildren(childUpdates2);

    }
    private String save_zgradu(String zgrada,String user){
        myRef= FirebaseDatabase.getInstance().getReference("Projekti/Zgrade");
        String key=myRef.push().getKey();
        zgrada_obj=new Zgrada(zgrada,user);
        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put( key, zgrada_obj);

        myRef.updateChildren(childUpdates);
        return key;


    }
    private String save_pod_zgradu(String pod_zgrada,String user,String zg_id){
        myRef= FirebaseDatabase.getInstance().getReference("Projekti/Podzgrade");
        String key=myRef.push().getKey();
        zgrada_obj=new Zgrada(pod_zgrada,user,zg_id);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put( key, zgrada_obj);
        myRef.updateChildren(childUpdates);
        return key;


    }
    private void save_pod_zgradu_update(String key, String podzgrada, String user, ArrayList<String> lista_liftova, String zg_id){
        myRef= FirebaseDatabase.getInstance().getReference("Projekti/Podzgrade");
        zgrada_obj=new Zgrada(user,podzgrada,lista_liftova,zg_id);
        Map<String, Object> childUpdates2 = new HashMap<>();
        childUpdates2.put( key, zgrada_obj);

        myRef.updateChildren(childUpdates2);

    }
    private String save_liftove(String zgrada, String podzgrada, String lift_naziv, String user) {
        myRef=database.getInstance().getReference("Liftovi");
        String key=myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        //id za zgradu i podzgradu dodati
        lift=new Lift(lift_naziv,zgrada,podzgrada,user,Integer.parseInt(n_k.getText().toString()),Integer.parseInt(v_k.getText().toString()),true);
        childUpdates.put( key, lift);
        myRef.updateChildren(childUpdates);
        return key;


    }

    private String save_liftove(String zgrada, String lift_naziv, String user){
        myRef=database.getInstance().getReference("Liftovi");
        String key=myRef.push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        //id za zgradu i podzgradu dodati
        lift=new Lift(lift_naziv,zgrada,user,Integer.parseInt(n_k.getText().toString()),Integer.parseInt(v_k.getText().toString()),true);
        childUpdates.put( key, lift);
        //System.out.println("TEST6 : "+childUpdates.toString());
        myRef.updateChildren(childUpdates);
        return key;

    }

}