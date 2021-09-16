package com.example.lift11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lift11.Mjerenje;
import com.example.lift11.Model.Dizalo;
import com.example.lift11.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IspisOldAdapter extends RecyclerView.Adapter<IspisOldAdapter.MyViewHolder>  {
    private Context mContext;
    private List<Dizalo> lifts_old;
    private SharedPreferences prefs;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    public IspisOldAdapter(Context context, List<Dizalo> uploads) {
        mContext = context;
        lifts_old = uploads;
    }

    @NotNull
    @Override
    public IspisOldAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.listview_old, parent, false);
        return new IspisOldAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IspisOldAdapter.MyViewHolder holder, int position) {
        final Dizalo listaDizala =lifts_old.get(position);
        prefs = Objects.requireNonNull(mContext).getSharedPreferences("shared_pref_name", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();


        if(listaDizala.getZg_ime()!=null){
            holder.zg_name.setText(listaDizala.getZg_ime());

        }
        if(listaDizala.getPod_ime()!=null){
            holder.pod_name.setText(listaDizala.getPod_ime());

        }
        holder.lift_name.setText(listaDizala.getIme());
        holder.itemView.setOnClickListener(v -> {
            myRef=database.getInstance().getReference("Liftovi");
            Map<String, Object> update = new HashMap<>();
            update.put("is_connected", true);
            myRef.child(listaDizala.getKey()).updateChildren(update);
            editor.putString("lift_id", listaDizala.getKey());
            editor.apply();
            Intent intent = new Intent(mContext, Mjerenje.class);
            intent.putExtra("id_lift", listaDizala.getKey());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return lifts_old.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView zg_name,pod_name,lift_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            zg_name = itemView.findViewById(R.id.nz_zg);
            pod_name=itemView.findViewById(R.id.podzg_old);
            lift_name=itemView.findViewById(R.id.lift_old);
        }
    }
}
