package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
    private Adresse adresse;

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
        editTextAdresse.setEnabled(false);

        if (!editmode){
            bestellungenButton.setEnabled(false);
            findViewById(R.id.button_kunde_delete).setEnabled(false);
            bestellungenButton.getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            (findViewById(R.id.button_kunde_delete)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
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
                    if (adresse == null){
                        adresse = datasource.getAdresse(datasource.getKunde(id).getAdresse());
                    }
                    datasource.updateKunde(KundenNummerLong, Name, adresse.getId(), KundeTyp);
                }else {
                    datasource.createKunde(Name, adresse.getId(), KundeTyp);
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

        Button deleteButton = findViewById(R.id.button_kunde_delete);
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

        Button selectAdresse = findViewById(R.id.button_kunde_select_adresse);
        selectAdresse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),AdressListeActivity.class);
                intent.putExtra(getString(R.string.kunde_editmode),false);
                startActivityForResult(intent,123);
            }
        });
    }

    private void fillPage(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.neuerKunde));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_my_calendar);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        if (id != 0L){
            Kunde kunde = datasource.getKunde(id);
            getSupportActionBar().setTitle(getResources().getString(R.string.titleKunde)+": " + id);
            editTextKundenNummer.setText(String.valueOf(id));
            editTextName.setText(kunde.getName());
            Adresse anzeigeAdresse = datasource.getAdresse(kunde.getAdresse());
            if(adresse != null){
                editTextAdresse.setText(adresse.toString());
            }else {
                editTextAdresse.setText(anzeigeAdresse.toString());
            }
            spinnerKundeTyp.setSelection(kunde.getKundenType().equals("Privatkunde")?0:1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!(resultCode==0)) {
            long id = data.getLongExtra("AdresseId", 0);
            if(!(id==0)) {
                datasource.open();
                adresse = datasource.getAdresse(id);
                editTextAdresse.setText(adresse.toString());
            }
        }
    }
}
