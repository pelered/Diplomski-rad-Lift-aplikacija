package com.example.lift11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Odabir extends AppCompatActivity {

    private Button novi,stari;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_odabir);
        novi=findViewById(R.id.novi);
        stari=findViewById(R.id.stari);
        novi.setOnClickListener(view -> {
            Intent intent = new Intent(this, DodajLift.class);
            startActivity(intent);
        });
        stari.setOnClickListener(view -> {
            Intent intent = new Intent(this, DodajPostojeci.class);
            startActivity(intent);

        });
    }
}