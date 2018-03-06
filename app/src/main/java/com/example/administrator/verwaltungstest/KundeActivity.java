package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//Klasse für die genauere Ansicht/das Bearbeiten eines Kunden
public class KundeActivity extends AppCompatActivity {

    private static final String TAG = KundeActivity.class.getSimpleName();

    private Button bestellungenButton;
    private EditText editTextKundenNummer, editTextName, editTextAdresse;
    private Spinner spinnerKundeTyp;
    private long id;
    private boolean editmode;
    private Datasource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kunden);

        datasource = new Datasource(this);

        editTextKundenNummer = findViewById(R.id.edit_KundenNummer);
        editTextName = findViewById(R.id.edit_Name);
        editTextAdresse = findViewById(R.id.edit_Adresse);
        spinnerKundeTyp = findViewById(R.id.spinner_KundeTyp);

        bestellungenButton = findViewById(R.id.button_orders_kunden);

        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_KUNDE_ID,0L);
        editTextKundenNummer.setEnabled(false);

        if (!editmode){
            bestellungenButton.setEnabled(false);
            findViewById(R.id.button_delete_kunden).setEnabled(false);
        }

        activateButtons();
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
        fillPage();
    }

    private void activateButtons() {
        Button saveButton = findViewById(R.id.button_save);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = editTextName.getText().toString();
                String Adresse = editTextAdresse.getText().toString();
                String KundeTyp = spinnerKundeTyp.getSelectedItem().toString();
                String KundenNummer = editTextKundenNummer.getText().toString();
                Long KundenNummerLong;

                if (!KundenNummer.equals("")){
                    KundenNummerLong = Long.parseLong(KundenNummer);
                }else {
                    KundenNummerLong = 1L;
                }

                if (TextUtils.isEmpty(Name)){
                    editTextName.setError(getString(R.string.editText_errorMessage));
                    editTextName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Adresse)){
                    editTextAdresse.setError(getString(R.string.editText_errorMessage));
                    editTextAdresse.requestFocus();
                    return;
                }

                if (editmode){
                    datasource.updateKunde(KundenNummerLong, Name, Adresse, KundeTyp);
                }else {
                    datasource.createKunde(KundenNummerLong, Name, Adresse, KundeTyp);
                }
                finish();
            }
        });

        TooltipCompat.setTooltipText(bestellungenButton,getString(R.string.hint_orders));
        bestellungenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KundeActivity.this,BestellungListeActivity.class);
                intent.putExtra("SelectMode",true);
                intent.putExtra("KundeId",id);
                startActivity(intent);
            }
        });

        Button deleteButton = findViewById(R.id.button_delete_kunden);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kunde kunde = datasource.getKunde(id);
                if (datasource.getAllBestellungen(id).size() == 0) {
                    datasource.deleteKunde(kunde);
                    finish();
                }else{
                    Toast.makeText(KundeActivity.this, getResources().getString(R.string.KundeDeleteError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillPage(){
        getSupportActionBar().setTitle(getResources().getString(R.string.neuerKunde));
        if (id != 0L){
            Kunde kunde = datasource.getKunde(id);
            getSupportActionBar().setTitle(getResources().getString(R.string.titleKunde)+": " + id);
            editTextKundenNummer.setText(String.valueOf(id));
            editTextName.setText(kunde.getName());
            editTextAdresse.setText(kunde.getAdresse());
            spinnerKundeTyp.setSelection(kunde.getKundenType().equals("Privatkunde")?0:1);
        }
    }
}
