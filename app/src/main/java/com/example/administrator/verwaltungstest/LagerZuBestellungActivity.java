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

//Klasse für das Hinzufügen eines Produktes auf einer Bestellung
public class LagerZuBestellungActivity extends AppCompatActivity {

    private static final String TAG = LagerZuBestellungActivity.class.getSimpleName();

    private EditText edit_lager_zu_bestellung_product,edit_lager_zu_bestellung_quantity;
    private Bestellung bestellung;
    private Product product;
    private long id;
    private boolean editmode;
    private Datasource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lager_zu_bestellung);

        datasource = new Datasource(this);

        edit_lager_zu_bestellung_product = findViewById(R.id.edit_lager_zu_bestellung_product);
        edit_lager_zu_bestellung_quantity = findViewById(R.id.edit_lager_zu_bestellung_quantity);

        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_KUNDE_ID,0L);
        edit_lager_zu_bestellung_product.setEnabled(false);

        if (!editmode){
            findViewById(R.id.button_delete_lager_zu_bestellung).setEnabled(false);
            (findViewById(R.id.button_delete_lager_zu_bestellung)).getBackground()
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
        Button selectProduct = findViewById(R.id.button_lager_zu_bestellung_select_product);
        selectProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ProductListeActivity.class);
                intent.putExtra("selectFlag",123);
                startActivityForResult(intent, 123);
            }
        });

        Button saveButton = findViewById(R.id.button_save_lager_zu_bestellung);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName = edit_lager_zu_bestellung_product.getText().toString();
                String quantity = edit_lager_zu_bestellung_quantity.getText().toString();

                if (TextUtils.isEmpty(productName)){
                    edit_lager_zu_bestellung_product.setError(getString(R.string.editText_errorMessage));
                    edit_lager_zu_bestellung_product.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(quantity)){
                    edit_lager_zu_bestellung_quantity.setError(getString(R.string.editText_errorMessage));
                    edit_lager_zu_bestellung_quantity.requestFocus();
                    return;
                }

                if (!editmode){
                    datasource.createLager_zu_Bestellung(bestellung.getId(), product.getId(),Integer.parseInt(quantity));
                }
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.button_delete_lager_zu_bestellung);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LagerZuBestellung lagerZuBestellung = datasource.getLager_zu_Bestellung(id);
                datasource.deleteLager_zu_Bestellung(lagerZuBestellung);
                finish();
            }
        });
    }

    private void fillPage(){

        bestellung = datasource.getBestellung(getIntent().getLongExtra("Bestellung",0));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.titleProdukt));
        }
        if (id != 0L){
            LagerZuBestellung lagerZuBestellung = datasource.getLager_zu_Bestellung(id);
            product = datasource.getProduct(lagerZuBestellung.getProduct().getId());
            edit_lager_zu_bestellung_product.setText(product.getName());
            edit_lager_zu_bestellung_quantity.setText(lagerZuBestellung.getQuantity());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!(resultCode==0)) {
            long id = data.getLongExtra("ProductId", 0);
            if(!(id==0)) {
                datasource.open();
                product = datasource.getProduct(id);
                edit_lager_zu_bestellung_product.setText(product.getName());
            }
        }
    }
}
