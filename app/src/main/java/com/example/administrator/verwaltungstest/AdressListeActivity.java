package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//Klasse zur anzeige und auswahl von Adressen in einer Liste
public class AdressListeActivity extends AppCompatActivity {

    private static final String TAG = AdressListeActivity.class.getSimpleName();

    private ListView adresseListView;
    private Datasource datasource;
    private Adresse slectedAdresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_liste);
        initializeAdressListView();
        datasource = new Datasource(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen");
        slectedAdresse = null;
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Die Datenquelle wird geöffnet");
        datasource.open();
        Log.d(TAG, "folgende Einträge sind in der DB vorhanden: ");
        getSupportActionBar().setTitle(getResources().getString(R.string.titleAdressen));
        showAllListEntries();
        //aktiviere bearbeiten und löschen wenn eine bereits vorhande Adresse ausgewählt wurde
        if (slectedAdresse == null) {
            findViewById(R.id.button_edit_adresse).setEnabled(false);
            findViewById(R.id.button_delete_adresse).setEnabled(false);
            findViewById(R.id.button_select_adresse).setEnabled(false);
            (findViewById(R.id.button_edit_adresse)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            (findViewById(R.id.button_delete_adresse)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            (findViewById(R.id.button_select_adresse)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
         } else {
            findViewById(R.id.button_edit_adresse).setEnabled(true);
            findViewById(R.id.button_delete_adresse).setEnabled(true);
            findViewById(R.id.button_select_adresse).setEnabled(true);
            (findViewById(R.id.button_edit_adresse)).getBackground().setColorFilter(null);
            (findViewById(R.id.button_delete_adresse)).getBackground().setColorFilter(null);
            (findViewById(R.id.button_select_adresse)).getBackground().setColorFilter(null);
         }
    }

    private void initializeAdressListView() {
        List<Adresse> emtyListForInitialisation = new ArrayList<>();

        adresseListView = findViewById(R.id.listview_adresse);
        adresseListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Adresse> adresseArrayAdapter = new ArrayAdapter<Adresse>(this,
                R.layout.list_item_basis,emtyListForInitialisation);
        adresseListView.setAdapter(adresseArrayAdapter);

        adresseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedAdresse = (Adresse) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_adresse).setEnabled(true);
                findViewById(R.id.button_delete_adresse).setEnabled(true);
                findViewById(R.id.button_select_adresse).setEnabled(true);
                (findViewById(R.id.button_edit_adresse)).getBackground().setColorFilter(null);
                (findViewById(R.id.button_delete_adresse)).getBackground().setColorFilter(null);
                (findViewById(R.id.button_select_adresse)).getBackground().setColorFilter(null);
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<Adresse> adresseList = datasource.getAllArdesse();
        ArrayAdapter<Adresse> adresseArrayAdapter =
                (ArrayAdapter<Adresse>) adresseListView.getAdapter();
        adresseArrayAdapter.clear();
        adresseArrayAdapter.addAll(adresseList);
        adresseArrayAdapter.notifyDataSetChanged();
    }

    //öfnnen der AdresseActivity zum hinzufügen einer neien Adresse
    public void adresseAnlegen(View view){
        Intent intent = new Intent(this, AdresseActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode), false);
        startActivity(intent);
    }

    //öfnnen der AdresseActivity im editierbaren Modus
    public void adresseEdit(View view){
        Intent intent = new Intent(this,AdresseActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_ADRESSE_ID,slectedAdresse.getId());
        startActivity(intent);
    }

    public void adresseDelete(View view){
        Adresse adresse = datasource.getAdresse(slectedAdresse.getId());
        //Prüfung auf noch vorhandende Kunden zu dieser Adresse
        if(datasource.getAllKunden(adresse.getId()).size() == 0){
            datasource.deleteAdresse(adresse);
        }else{
            Toast.makeText(AdressListeActivity.this, getResources().getString(R.string.AdresseDeleteError), Toast.LENGTH_SHORT).show();
        }
        showAllListEntries();
    }

    public void selectAdresse(View view){
        long id = slectedAdresse.getId();
        Intent intent = new Intent();
        intent.putExtra("AdresseId",id);
        setResult(RESULT_OK,intent);
        finish();
    }
}
