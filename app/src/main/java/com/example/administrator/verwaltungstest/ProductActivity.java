package com.example.administrator.verwaltungstest;

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
import android.widget.Toast;

//Klasse für die genauere Ansicht/das Bearbeiten eines Produktes
public class ProductActivity extends AppCompatActivity {

    private static final String TAG = ProductActivity.class.getSimpleName();

    private EditText editTextProductNummer, editTextName, editTextQuantity, editTextPreis;
    private long id;
    private boolean editmode;
    private Datasource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        datasource = new Datasource(this);

        editTextProductNummer = findViewById(R.id.edit_product_Id);
        editTextName = findViewById(R.id.edit_product_Name);
        editTextQuantity = findViewById(R.id.edit_product_Quantity);
        editTextPreis = findViewById(R.id.editProductPreis);

        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_PRODUCT_ID,0L);
        editTextProductNummer.setEnabled(false);

        editTextPreis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                String text = editTextPreis.getText().toString();
                if((text.length() != 0) && !text.substring(text.length() - 1).equals("€")){
                    String displayText = text + "€";
                    editTextPreis.setText(displayText);
                }
            }
        });

        if (!editmode){
            findViewById(R.id.button_delete_products).setEnabled(false);
            (findViewById(R.id.button_delete_products)).getBackground()
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
        final Button saveButton = findViewById(R.id.button_save_products);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name = editTextName.getText().toString();
                String Quantity = editTextQuantity.getText().toString();
                String ProductNummer = editTextProductNummer.getText().toString();
                String Preis = editTextPreis.getText().toString();
                Double PreisDouble;
                if(Preis.length() == 1){
                    PreisDouble = Double.parseDouble(Preis);
                }else {
                    if (Preis.substring(Preis.length() - 1).equals("€")) {
                        PreisDouble = Double.parseDouble(Preis.substring(0, Preis.length() - 1));
                    }else{
                        PreisDouble = Double.parseDouble(Preis);
                    }
                }
                Long ProductNummerLong;

                if (!ProductNummer.equals("")){
                    ProductNummerLong = Long.parseLong(ProductNummer);
                }else {
                    ProductNummerLong = 1L;
                }

                if (TextUtils.isEmpty(Name)){
                    editTextName.setError(getString(R.string.editText_errorMessage));
                    editTextName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Quantity)){
                    editTextQuantity.setError(getString(R.string.editText_errorMessage));
                    editTextQuantity.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(Preis)){
                    editTextPreis.setError(getString(R.string.editText_errorMessage));
                    editTextPreis.requestFocus();
                    return;
                }

                if (editmode){
                    datasource.updateProduct(ProductNummerLong, Name, Integer.parseInt(Quantity),PreisDouble);
                }else {
                    datasource.createProduct(Name, Integer.parseInt(Quantity),PreisDouble);
                }
                finish();
            }
        });

        Button deleteButton = findViewById(R.id.button_delete_products);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Product product = datasource.getProduct(id);
                if(datasource.getAllLager_zu_Bestellungen_Product(id).size() == 0) {
                    datasource.deleteProduct(product);
                    finish();
                }else{
                    Toast.makeText(ProductActivity.this, getResources().getString(R.string.ProduktDeleteError), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fillPage(){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.neuesProdukt));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_sort_by_size);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        if (id != 0L){
            Product product = datasource.getProduct(id);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getResources().getString(R.string.titleProdukt) + ": " + id);
            }
            editTextProductNummer.setText(String.valueOf(id));
            editTextName.setText(product.getName());
            editTextQuantity.setText(String.valueOf(product.getQuantity()));
            String text = String.valueOf(product.getPreis()) + "€";
            editTextPreis.setText(text);
        }
    }
}
