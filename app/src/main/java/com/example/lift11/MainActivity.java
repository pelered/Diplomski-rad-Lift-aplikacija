  package com.example.lift11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;

import com.example.lift11.Model.Lift;
import com.example.lift11.Model.Lift_travels;

import com.example.lift11.Model.User;
import com.example.lift11.Model.Zgrada;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

  public class MainActivity extends AppCompatActivity {
    private Button start,end,generiraj_lift;
      private static final long START_TIME_IN_MILLIS = 5000;
      private int p_k;//pocetni kat
      private int z_k;//zavrsni kat

      private int n_k;//najnizi kat
      private int v_k;//najvisi kat

      private String start_time;
      private String end_time;

      private int count_p;//broj ljudi

      private String odb_zgrada;
      private String odb_pod_zgrada;
      private String odb_key;
      private Lift_travels liftTravels;
      private Lift lift;
      private CountDownTimer mCountDownTimer;
      private User user;
      private Zgrada zgrada_obj;
      //private boolean isTimerRunning;



      ArrayList<String> zgrada = new ArrayList<String>(); //
      ArrayList<String> pod_zgrada = new ArrayList<String>(); // Create an ArrayList object
      ArrayList<String> lista_liftova ; // Create an ArrayList object

      private FirebaseDatabase database;
      private DatabaseReference myRef;

      private boolean vrti= false;
      private ArrayList <User> user_list;
      private ArrayList<Lift> liftovi;


      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
          start=findViewById(R.id.start);
          end=findViewById(R.id.end);
          generiraj_lift=findViewById(R.id.buttn_lift);
          dodaj_u_listu_zgrade();
          user_list=new ArrayList<>();
          liftovi=new ArrayList<>();
          get_users();

          generiraj_lift.setOnClickListener(view -> {
              generate_liftove();
          });

          start.setOnClickListener(view -> {
              //ako se vrti
              //System.out.println("TEST0: "+isTimerRunning);
              myRef=database.getInstance().getReference("Liftovi");
              Log.d("TAGput0:",myRef.toString());
              myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull  DataSnapshot snapshot) {
                      for(DataSnapshot recipeSnapshot: snapshot.getChildren()){
                          //Log.d("TAG2 ",recipeSnapshot.toString());
                          //Log.d("TAGput0.5:",recipeSnapshot.toString());
                          liftovi.add(recipeSnapshot.getValue(Lift.class));
                          Log.d("KEY1:", String.valueOf(liftovi.size()));
                          Log.d("KEY2:",liftovi.get(liftovi.size()-1).toString());

                          liftovi.get(liftovi.size()-1).setKey(recipeSnapshot.getKey());
                      }
                      Log.d("TAGput1:",liftovi.toString());
                      postavi_vrijednosti();
                  }

                  @Override
                  public void onCancelled(@NonNull  DatabaseError error) {
                      Log.d("TAGput0.5:",error.toString());
                  }
              });
              /*if(!vrti){
                  postavi_vrijednosti();
              }*/
          });
          end.setOnClickListener(view -> {
              vrti=false;
          });


      }

      private void postavi_vrijednosti() {
          vrti=true;
          int odabrani_lift=getRandomNumber(0,liftovi.size()-1);
          //Log.d("TAGput2:",liftovi.toString());
          //Log.d("TAGput3:", String.valueOf(odabrani_lift));
          odb_zgrada= liftovi.get(odabrani_lift).getZgrada();
          odb_pod_zgrada=liftovi.get(odabrani_lift).getPod_zg();
          n_k=liftovi.get(odabrani_lift).getN_k();
          v_k=liftovi.get(odabrani_lift).getV_k();
          odb_key=liftovi.get(odabrani_lift).getKey();
          Log.d("KEY",liftovi.toString());



          p_k=getRandomNumber(n_k,v_k);
          z_k=getRandomNumber(p_k,v_k);
          //System.out.println("TEST5: "+p_k);
          //System.out.println("TEST6:"+z_k);
          while(p_k==z_k){
              z_k=getRandomNumber(p_k,(v_k+1));
              //System.out.println("TEST7: "+","+p_k+","+v_k+","+z_k);
          }

          start_timer();
      }



      private void start_timer() {

          //System.out.println("TEST8 :");
          count_p=getRandomNumber(0,6);
          start_time= Calendar.getInstance().getTime().toString();
          mCountDownTimer = new CountDownTimer(v_k*1000, 1000) {
              @Override
              public void onTick(long l) {

                  //ujedno salje info na webstranicu ako gledas taj liftTravels

              }
              @Override
              public void onFinish() {
                  end_time=Calendar.getInstance().getTime().toString();
                  liftTravels =new Lift_travels(p_k,z_k,n_k,v_k,start_time,end_time,count_p,odb_zgrada,odb_pod_zgrada,odb_key);
                  //System.out.println("TEST: "+liftTravels.toString());
                  save_data_travels();
                  if(vrti){
                      postavi_vrijednosti();
                  }
                  //postavi_vrijednosti();
                  //pokreni ponovo ako nije pritisnut stop gumb

              }
          }.start();

          //System.out.println("TESTTTTT: ");

      }
    private void get_users(){
        myRef=database.getInstance().getReference("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                //Log.d("TAG1 ",snapshot.toString());

                for(DataSnapshot recipeSnapshot: snapshot.getChildren()){
                    //Log.d("TAG2 ",recipeSnapshot.toString());
                    user_list.add(recipeSnapshot.getValue(User.class));
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
      private void generate_liftove(){
          ArrayList<String> lista_podzgrada=new ArrayList<>();
          ArrayList<String> lista_zgrada=new ArrayList<>();
          lista_liftova=new ArrayList<>();
          String user,podzgrada;
          String key,key2;
          Integer count_lift,count_pod_zg;

          for(int i=0; i<zgrada.size();i++){
              Log.d("TAG1", String.valueOf(i));
              user=user_list.get(getRandomNumber(0,user_list.size()-1)).getUid();
              //spremimo ZGRADU ODABRANU  u bazu da dobimo id zgrade za spremanje u lift

              key=save_zgradu(zgrada.get(i),user);
              //sprema se lista ideva zgrada
              lista_zgrada.add(key);
              //sve zgrade prolazimo
              //biramo hoce li imati podzgradu ili ne
              if(getRandomNumber(0,2)==1){
                  Log.d("TAG3"," podzgrada usao");
                  //imamo podzgrad
                  //odabrati koliko podzgrada ima
                  count_pod_zg=getRandomNumber(0,4);
                  for(int k=0;k<count_pod_zg;k++) {
                      //Log.d("TAG4", "podzgrada broj: "+String.valueOf(k));

                      //posjecujemo sve podzgrade

                      //biramo podzgradu,provjera da vec nije odabrana
                      podzgrada = "pod"+k;

                      //dodajemo podzgradu u bazu da dobijemo id za spremiti kod lifta

                      //dodati id podzgrade u listu
                      key2=save_pod_zgradu(podzgrada,user,key);
                      lista_podzgrada.add(key2);
                      Log.d("TAG6", "podzgrada broj:"+String.valueOf(k));
                      //biramo broj liftova
                      count_lift = getRandomNumber(1, 5);
                      //spremamo podzgradu da dobijemo id
                      //save_zgrade_pod_zg(podzgrada,user);
                      for (int j = 0; j < count_lift; j++) {
                          //dodajemo svaki Lift u bazu,prvo zgrade i podzgrade spremiti da
                          //dobijem id
                          //save_zgrade_pod_zg(podzgrada);
                          lista_liftova.add(save_liftove(key,key2,j,user));
                          Log.d("TAG6", "podzgrada broj:"+String.valueOf(k)+"//"+lista_liftova);

                      }

                      Log.d("TAG7", "podzgrada broj:"+key2+podzgrada+user+lista_liftova+"ovo je kljuc"+key);

                      //azuriramo pojedinu podzgradu s novim podacima
                      save_zgrade_pod_zg(key2,podzgrada,user,lista_liftova,key);
                      //lista_liftova.clear();



                  }
                  //azuriramo u bazi zgradu s podacima

                  save_zgrade_update(key,zgrada.get(i),user,lista_podzgrada,lista_liftova);
                  lista_podzgrada.clear();
                  lista_liftova.clear();


              }else{
                  //nema podzgradu


                  count_lift = getRandomNumber(1, 5);
                  //biramo broj liftova u zgradi
                  for (int j = 0; j < count_lift; j++) {
                      //dodajemo svaki Lift u bazu,prvo zgrade i podzgrade spremiti da
                      //dobijem id
                      lista_liftova.add(save_liftove(key,j,user));
                      Log.d("TAG7", "podzgrada broj:"+lista_liftova.toString());

                  }
                  //azuriramo u bazi zgradu s podacima
                  //myRef=database.getInstance().getReference("Projekti");
                  //key=myRef.push().getKey();
                  //zgrada_obj.setLifts(lista_liftova);
                  Map<String, Object>  childUpdates4;

                  myRef=database.getInstance().getReference("Projekti/Zgrade");
                  zgrada_obj=new Zgrada(user,zgrada.get(i),lista_liftova);
                  childUpdates4 = new HashMap<>();
                  childUpdates4.put( key, zgrada_obj);
                 myRef.updateChildren(childUpdates4).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull  Task<Void> task) {
                          Log.d("TAG2-22put", childUpdates4.toString()+"onComplete: "+task);
                      }
                  });

                  lista_liftova.clear();

              }
          }

      }

      private void save_zgrade_update(String key,String zgrada,String user,ArrayList<String> pod_zgrada,ArrayList<String> lista_liftova) {
          myRef=database.getInstance().getReference("Projekti/Zgrade");

          Log.d("TAG8", "1");
          zgrada_obj=new Zgrada(zgrada,user,pod_zgrada,lista_liftova);
          Map<String, Object> childUpdates2 = new HashMap<>();
          childUpdates2.put( key, zgrada_obj);
          myRef.updateChildren(childUpdates2).addOnCompleteListener(task -> {

          });

      }
      private String save_zgradu(String zgrada,String user){
          myRef= FirebaseDatabase.getInstance().getReference("Projekti/Zgrade");
          String key=myRef.push().getKey();
          zgrada_obj=new Zgrada(zgrada,user);
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put( key, zgrada_obj);
          myRef.updateChildren(childUpdates).addOnCompleteListener(
                  task -> {Log.d("TAG2",childUpdates.toString()+ task.toString());
                  });
          return key;

      }
      private String save_pod_zgradu(String pod_zgrada,String user,String zg_id){
          myRef= FirebaseDatabase.getInstance().getReference("Projekti/Podzgrade");
          String key=myRef.push().getKey();
          zgrada_obj=new Zgrada(pod_zgrada,user,zg_id);
          Map<String, Object> childUpdates = new HashMap<>();
          Log.d("TAG5.1",key+zgrada_obj.toString());
          childUpdates.put( key, zgrada_obj);
          myRef.updateChildren(childUpdates).addOnCompleteListener(
                  task -> {Log.d("TAG5",childUpdates.toString()+ task.toString());
                  });
          return key;

      }
      private void save_zgrade_pod_zg(String key, String podzgrada,String user,ArrayList<String> lista_liftova,String zg_id){
          myRef= FirebaseDatabase.getInstance().getReference("Projekti/Podzgrade");
          zgrada_obj=new Zgrada(user,podzgrada,lista_liftova,zg_id);
          Map<String, Object> childUpdates2 = new HashMap<>();
          childUpdates2.put( key, zgrada_obj);
          Log.d("TAGpodzgrada:",childUpdates2.toString());

          myRef.updateChildren(childUpdates2).addOnCompleteListener(task -> {
              Log.d("TAG55",childUpdates2.toString()+ task.toString());
          });

      }
      private String save_liftove(String s, String podzgrada, int j, String user) {
          myRef=database.getInstance().getReference("Liftovi");
          String key=myRef.push().getKey();
          Map<String, Object> childUpdates = new HashMap<>();

          int n_kk=getRandomNumber(-5,0);
          int v_kk=getRandomNumber(n_kk,20);

          while((n_kk+2) > (v_kk)){
              v_kk=getRandomNumber(n_kk,20);
          }
          //id za zgradu i podzgradu dodati

          lift=new Lift("Lift"+j,s,podzgrada,user,n_kk,v_kk);
          childUpdates.put( key, lift);
          //System.out.println("TEST6 : "+childUpdates.toString());
          myRef.updateChildren(childUpdates);
          return key;


      }

      private String save_liftove(String s, int j, String user){
          myRef=database.getInstance().getReference("Liftovi");
          String key=myRef.push().getKey();
          Map<String, Object> childUpdates = new HashMap<>();

          int n_kk=getRandomNumber(-5,0);
          int v_kk=getRandomNumber(n_kk,20);

          while((n_kk+2) > (v_kk)){
              v_kk=getRandomNumber(n_kk,20);
          }
          //id za zgradu i podzgradu dodati

          lift=new Lift("Lift"+j,s,user,n_kk,v_kk);
          childUpdates.put( key, lift);
          //System.out.println("TEST6 : "+childUpdates.toString());
          myRef.updateChildren(childUpdates);
          return key;

      }


      private void save_data_travels() {
          myRef=database.getInstance().getReference("Putovanja");
          //System.out.println("TEST5 : "+myRef.toString());
          String key=myRef.push().getKey();
          Map<String, Object> childUpdates = new HashMap<>();
          childUpdates.put( key, liftTravels);
          //System.out.println("TEST6 : "+childUpdates.toString());
          myRef.updateChildren(childUpdates);
          //myRef.child("petra").setpush();
      }

      private void dodaj_u_listu_zgrade() {
          zgrada.add("bolnica1");
          zgrada.add("bolnica2");
          zgrada.add("bolnica3");
          zgrada.add("faks1");
          zgrada.add("faks2");
          zgrada.add("trgovacki centar");


          pod_zgrada.add("pod1");
          pod_zgrada.add("pod2");
          pod_zgrada.add("pod3");
          pod_zgrada.add("pod4");
      }


      public int getRandomNumber(int min, int max) {
          return (int) ((Math.random() * (max - min)) + min);
      }
  }