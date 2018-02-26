package com.example.administrator.verwaltungstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BestellungActivity extends AppCompatActivity {

    private static final String TAG = ProductActivity.class.getSimpleName();

    private EditText editTextBestellunngsNummer, editTextKunde;
    private long id;
    private boolean editmode;
    private Datasource datasource;
    private Kunde kunde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        datasource = new Datasource(this);

        editTextBestellunngsNummer = findViewById(R.id.edit_product_Id);
        editTextKunde = findViewById(R.id.edit_product_Name);

        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_BESTELLUNG_ID,0L);
        editTextBestellunngsNummer.setEnabled(false);

        if (!editmode){
            findViewById(R.id.button_delete_bestellung).setEnabled(false);
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
        Button saveButton = findViewById(R.id.button_save_products);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String KundeName = editTextKunde.getText().toString();
                String ProductNummer = editTextBestellunngsNummer.getText().toString();
                Long ProductNummerLong;

                if (!ProductNummer.equals("")){
                    ProductNummerLong = Long.parseLong(ProductNummer);
                }else {
                    ProductNummerLong = 1L;
                }

                if (TextUtils.isEmpty(KundeName)){
                    editTextKunde.setError(getString(R.string.editText_errorMessage));
                    editTextKunde.requestFocus();
                    return;
                }

                if (editmode){
                    datasource.updateBestellung(ProductNummerLong, (int)kunde.getId());
                }else {
                    datasource.createBestellung(ProductNummerLong, (int)kunde.getId());
                }
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.button_delete_products);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = datasource.getProduct(id);
                datasource.deleteProduct(product);
                finish();
            }
        });
    }

    private void fillPage(){

        if (id != 0L){
            Bestellung bestellung = datasource.getBestellung(id);
            editTextBestellunngsNummer.setText(String.valueOf(id));
            if (editmode) {
                kunde = bestellung.getKunde();
                editTextKunde.setText(kunde.getName());
            }
        }
    }
}
