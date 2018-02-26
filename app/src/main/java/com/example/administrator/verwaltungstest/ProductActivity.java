package com.example.administrator.verwaltungstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ProductActivity extends AppCompatActivity {

    private static final String TAG = KundeActivity.class.getSimpleName();

    private EditText editTextProductNummer, editTextName, editTextQuantity;
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


        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_PRODUCT_ID,0L);
        editTextProductNummer.setEnabled(false);

        if (!editmode){
            findViewById(R.id.button_delete_products).setEnabled(false);
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
                String Quantity = editTextQuantity.getText().toString();
                String ProductNummer = editTextProductNummer.getText().toString();
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

                if (editmode){
                    datasource.updateProduct(ProductNummerLong, Name, Integer.parseInt(Quantity));
                }else {
                    datasource.createProduct(ProductNummerLong, Name, Integer.parseInt(Quantity));
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
            Product product = datasource.getProduct(id);
            editTextProductNummer.setText(String.valueOf(id));
            editTextName.setText(product.getName());
            editTextQuantity.setText(product.getQuantity());
        }
    }
}
