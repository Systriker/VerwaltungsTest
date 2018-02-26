package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class BestellungListeActivity extends AppCompatActivity {

    private static final String TAG = ProductListeActivity.class.getSimpleName();

    private ListView bestellungListView;
    private Datasource datasource;
    private Bestellung slectedBestellung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestellung_liste);
        initializeKundeListView();
        datasource = new Datasource(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen");
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Die Datenquelle wird geöffnet");
        datasource.open();
        Log.d(TAG, "folgende Einträge sind in der DB vorhanden: ");
        showAllListEntries();
        if (slectedBestellung == null){
            findViewById(R.id.button_edit_bestellung).setEnabled(false);
            findViewById(R.id.button_delete_bestellung).setEnabled(false);
        }else {
            findViewById(R.id.button_edit_bestellung).setEnabled(true);
            findViewById(R.id.button_delete_bestellung).setEnabled(true);
        }
    }

    private void initializeKundeListView() {
        List<Bestellung> emtyListForInitialisation = new ArrayList<>();

        bestellungListView = findViewById(R.id.listview_products);
        bestellungListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Bestellung> bestellungArrayAdapter = new ArrayAdapter<Bestellung>(this,
                android.R.layout.simple_list_item_single_choice,emtyListForInitialisation);
        bestellungListView.setAdapter(bestellungArrayAdapter);

        bestellungListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedBestellung = (Bestellung) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_bestellung).setEnabled(true);
                findViewById(R.id.button_delete_bestellung).setEnabled(true);
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<Bestellung> bestellungtList = datasource.getAllBestellungen();
        ArrayAdapter<Bestellung> bestellungArrayAdapter =
                (ArrayAdapter<Bestellung>) bestellungListView.getAdapter();
        bestellungArrayAdapter.clear();
        bestellungArrayAdapter.addAll(bestellungtList);
        bestellungArrayAdapter.notifyDataSetChanged();
    }

    public void productAnlegen(View view){
        Intent intent = new Intent(this,BestellungActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),false);
        startActivity(intent);
    }

    public void productEdit(View view){
        Intent intent = new Intent(this,BestellungActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_BESTELLUNG_ID,slectedBestellung.getId());
        startActivity(intent);
    }

    public void productDelete(View view){
        Bestellung bestellung = datasource.getBestellung(slectedBestellung.getId());
        datasource.deleteBestellung(bestellung);
        showAllListEntries();
    }
}