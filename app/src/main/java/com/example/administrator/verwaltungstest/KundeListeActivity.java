package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//Klasse zur anzeige und auswahl von Kunden in einer Liste
public class KundeListeActivity extends AppCompatActivity {

    private static final String TAG = KundeListeActivity.class.getSimpleName();

    private ListView kundeListView;
    private Datasource datasource;
    private Kunde slectedKunde;
    private int selectFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunde_liste);
        initializeKundeListView();
        datasource = new Datasource(this);

        //wenn die Liste zur auswahl eines Kunden genutzt wird
        selectFlag = getIntent().getIntExtra("selectFlag",0);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.button_kunden));
        showAllListEntries();
        //wenn die Liste zur auswahl genutzt wird werden die editier und löschen Buttons ausgeblendet
        //und der hinzufügen Button als bestätigungs Button verwendet
        if (selectFlag == 123){
            findViewById(R.id.button_edit_kunden).setEnabled(false);
            findViewById(R.id.button_delete_kunde).setEnabled(false);
            findViewById(R.id.button_edit_kunden).setVisibility(Button.INVISIBLE);
            findViewById(R.id.button_delete_kunde).setVisibility(Button.INVISIBLE);
            ((Button)findViewById(R.id.button_add_kunde)).setText("Ok");
        }else {
            //aktiviere bearbeiten und löschen wenn ein bereits vorhander Kunde ausgewählt wurde
            if (slectedKunde == null) {
                findViewById(R.id.button_edit_kunden).setEnabled(false);
                findViewById(R.id.button_delete_kunde).setEnabled(false);
            } else {
                findViewById(R.id.button_edit_kunden).setEnabled(true);
                findViewById(R.id.button_delete_kunde).setEnabled(true);
            }
        }
    }

    private void initializeKundeListView() {
        List<Kunde> emtyListForInitialisation = new ArrayList<>();

        kundeListView = findViewById(R.id.listview_kunden);
        kundeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Kunde> kundeArrayAdapter = new ArrayAdapter<Kunde>(this,
                android.R.layout.simple_list_item_single_choice,emtyListForInitialisation);
        kundeListView.setAdapter(kundeArrayAdapter);

        kundeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedKunde = (Kunde) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_kunden).setEnabled(true);
                findViewById(R.id.button_delete_kunde).setEnabled(true);
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<Kunde> kundeList = datasource.getAllKunde();
        ArrayAdapter<Kunde> kundeArrayAdapter =
                (ArrayAdapter<Kunde>) kundeListView.getAdapter();
        kundeArrayAdapter.clear();
        kundeArrayAdapter.addAll(kundeList);
        kundeArrayAdapter.notifyDataSetChanged();
    }

    public void kundeAnlegen(View view){
        //wenn die Liste zur auswahl genutzt wird, wird der
        //hinzufügen Button als bestätigungs Button verwendet
        if (selectFlag == 123){
            selectKunde();
        }else {
            Intent intent = new Intent(this, KundeActivity.class);
            intent.putExtra(getString(R.string.kunde_editmode), false);
            startActivity(intent);
        }
    }

    //öfnnen der KundeActivity im editierbaren Modus
    public void kundeEdit(View view){
        Intent intent = new Intent(this,KundeActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_KUNDE_ID,slectedKunde.getId());
        startActivity(intent);
    }

    public void kundeDelete(View view){
        Kunde kunde = datasource.getKunde(slectedKunde.getId());
        //Prüfung auf noch vorhandende Bestellungen zu diesem Kunden
        if(datasource.getAllBestellungen(kunde.getId()).size() == 0){
            datasource.deleteKunde(kunde);
        }else{
            Toast.makeText(KundeListeActivity.this, getResources().getString(R.string.KundeDeleteError), Toast.LENGTH_SHORT).show();
        }
        showAllListEntries();
    }

    public void selectKunde(){
        long id = slectedKunde.getId();
        Intent intent = new Intent();
        intent.putExtra("KundeId",id);
        setResult(RESULT_OK,intent);
        finish();
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
