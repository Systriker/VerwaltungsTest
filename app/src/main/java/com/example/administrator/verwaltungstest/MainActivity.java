package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void kundeClick(View view){
        startActivity(new Intent(this,KundeListeActivity.class));
    }

    public void lagerClick(View view){
        startActivity(new Intent(this,ProductListeActivity.class));
    }

    public void bestellungenClick(View view){
        startActivity(new Intent(this,BestellungListeActivity.class));
    }
}
