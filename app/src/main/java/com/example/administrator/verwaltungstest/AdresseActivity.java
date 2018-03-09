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

public class AdresseActivity extends AppCompatActivity {

    private static final String TAG = AdresseActivity.class.getSimpleName();

    private EditText editTextStraße, editTextHausnr, editTextZusatz, editTextOrt, editTextPLZ;
    private long id;
    private boolean editmode;
    private Datasource datasource;
    private Adresse adresse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adresse);

        datasource = new Datasource(this);

        editTextStraße = findViewById(R.id.edit_adresse_straße);
        editTextHausnr = findViewById(R.id.edit_adresse_hausnr);
        editTextZusatz = findViewById(R.id.edit_adresse_zusatz);
        editTextOrt = findViewById(R.id.edit_adresse_ort);
        editTextPLZ = findViewById(R.id.edit_adresse_plz);


        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_ADRESSE_ID,0L);

        if (!editmode){
            findViewById(R.id.adresse_button_delete).setEnabled(false);
            (findViewById(R.id.adresse_button_delete)).getBackground()
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
        Button saveButton = findViewById(R.id.adresse_button_save);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TextStraße = editTextStraße.getText().toString();
                String TextHausnr = editTextHausnr.getText().toString();
                int intHausnr = Integer.parseInt(TextHausnr);
                String TextZusatz = editTextZusatz.getText().toString();
                String TextOrt = editTextOrt.getText().toString();
                String TextPLZ = editTextPLZ.getText().toString();
                long longPLZ =  Long.parseLong(TextPLZ);

                if (TextUtils.isEmpty(TextStraße)){
                    editTextStraße.setError(getString(R.string.editText_errorMessage));
                    editTextStraße.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(TextHausnr)){
                    editTextHausnr.setError(getString(R.string.editText_errorMessage));
                    editTextHausnr.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(TextZusatz)){
                    TextZusatz = "";
                }

                if (TextUtils.isEmpty(TextOrt)){
                    editTextOrt.setError(getString(R.string.editText_errorMessage));
                    editTextOrt.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(TextPLZ)){
                    editTextPLZ.setError(getString(R.string.editText_errorMessage));
                    editTextPLZ.requestFocus();
                    return;
                }

                if (editmode){
                    datasource.updateAdresse(adresse.getId(), TextStraße, intHausnr, TextZusatz, TextOrt, longPLZ);
                }else {
                    datasource.createAdresse(id, TextStraße, intHausnr, TextZusatz, TextOrt, longPLZ);
                }
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.adresse_button_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Adresse adresse = datasource.getAdresse(id);
                if (datasource.getAllKunden(id).size() == 0) {
                    datasource.deleteAdresse(adresse);
                    finish();
                }else{
                    Toast.makeText(AdresseActivity.this, getResources().getString(R.string.AdresseDeleteError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillPage(){
        getSupportActionBar().setTitle(getResources().getString(R.string.neueAdresse));
        if (id != 0L){
            Adresse adresse = datasource.getAdresse(id);
            getSupportActionBar().setTitle(getResources().getString(R.string.titleAdresse)+": " + id);
            editTextStraße.setText(adresse.getStraße());
            editTextHausnr.setText(String.valueOf(adresse.getHausnummer()));
            editTextZusatz.setText(adresse.getZusatz());
            editTextOrt.setText(adresse.getOrt());
            editTextPLZ.setText(String.valueOf(adresse.getPlz()));
        }
    }
}
