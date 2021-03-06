package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//Klasse zur anzeige und auswahl von Bestellungen in einer Liste
public class BestellungListeActivity extends AppCompatActivity {

    private static final String TAG = ProductListeActivity.class.getSimpleName();

    private ListView bestellungListView;
    private Datasource datasource;
    private Bestellung slectedBestellung;
    private boolean selectMode;
    private long kunde_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestellung_liste);
        initializeBestellungListView();
        datasource = new Datasource(this);

        //wenn die Liste zur auswahl einer Bestellung genutzt wird
        selectMode = getIntent().getBooleanExtra("SelectMode",false);
        kunde_id = getIntent().getLongExtra("KundeId",0);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen");
        slectedBestellung = null;
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Die Datenquelle wird geöffnet");
        datasource.open();
        Log.d(TAG, "folgende Einträge sind in der DB vorhanden: ");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.button_bestellungen));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_agenda);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        showAllListEntries();
        //aktiviere bearbeiten und löschen wenn eine bereits vorhande Bestellung ausgewählt wurde
        if (slectedBestellung == null){
            findViewById(R.id.button_edit_bestellung).setEnabled(false);
            findViewById(R.id.button_delete_bestellung).setEnabled(false);
            (findViewById(R.id.button_edit_bestellung)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            (findViewById(R.id.button_delete_bestellung)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }else {
            findViewById(R.id.button_edit_bestellung).setEnabled(true);
            findViewById(R.id.button_delete_bestellung).setEnabled(true);
            (findViewById(R.id.button_edit_bestellung)).getBackground().setColorFilter(null);
            (findViewById(R.id.button_delete_bestellung)).getBackground().setColorFilter(null);
        }
    }

    private void initializeBestellungListView() {
        List<Bestellung> emtyListForInitialisation = new ArrayList<>();

        bestellungListView = findViewById(R.id.listview_bestellung);
        bestellungListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Bestellung> bestellungArrayAdapter = new ArrayAdapter<>(this,
                R.layout.list_item_basis,emtyListForInitialisation);
        bestellungListView.setAdapter(bestellungArrayAdapter);

        bestellungListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedBestellung = (Bestellung) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_bestellung).setEnabled(true);
                findViewById(R.id.button_delete_bestellung).setEnabled(true);
                (findViewById(R.id.button_edit_bestellung)).getBackground().setColorFilter(null);
                (findViewById(R.id.button_delete_bestellung)).getBackground().setColorFilter(null);
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<Bestellung> bestellungtList;
        if (selectMode){
            bestellungtList = datasource.getAllBestellungen(kunde_id);
        }else {
            bestellungtList = datasource.getAllBestellungen();
        }
        ArrayAdapter<Bestellung> bestellungArrayAdapter =
                (ArrayAdapter<Bestellung>) bestellungListView.getAdapter();
        bestellungArrayAdapter.clear();
        bestellungArrayAdapter.addAll(bestellungtList);
        bestellungArrayAdapter.notifyDataSetChanged();
    }

    public void bestellungAnlegen(View view){
        Intent intent = new Intent(this,BestellungActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),false);
        startActivity(intent);
    }

    //öfnnen der BestellungActivity im editierbaren Modus
    public void bestellungEdit(View view){
        Intent intent = new Intent(this,BestellungActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_BESTELLUNG_ID,slectedBestellung.getId());
        startActivity(intent);
    }

    public void bestellungDelete(View view){
        Bestellung bestellung = datasource.getBestellung(slectedBestellung.getId());
        //Prüfung auf noch vorhandende Produkte auf dieser Bestellung
        if(datasource.getAllLager_zu_Bestellungen(bestellung.getId()).size() == 0){
            datasource.deleteBestellung(bestellung);
        }else{
            Toast.makeText(BestellungListeActivity.this,
                    getResources().getString(R.string.BestellungDeleteError), Toast.LENGTH_SHORT).show();
        }
        showAllListEntries();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //setzen des Home-Buttons so das er immer auf die letze Aktivty zurückkehrt
        // da es mehrere Wege gibt um auf diese Aktivity zuzugreifen
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
