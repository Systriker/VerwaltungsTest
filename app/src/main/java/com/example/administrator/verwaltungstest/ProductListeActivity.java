package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProductListeActivity extends AppCompatActivity {

    private static final String TAG = ProductListeActivity.class.getSimpleName();

    private ListView productListView;
    private Datasource datasource;
    private Product slectedProduct;
    private int selectFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_liste);
        initializeKundeListView();
        datasource = new Datasource(this);
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
        showAllListEntries();
        if (selectFlag == 123){
            findViewById(R.id.button_edit_product).setEnabled(false);
            findViewById(R.id.button_delete_product).setEnabled(false);
            findViewById(R.id.button_edit_product).setVisibility(Button.INVISIBLE);
            findViewById(R.id.button_delete_product).setVisibility(Button.INVISIBLE);
            ((Button)findViewById(R.id.button_add_product)).setText("Ok");
        }else {
            if (slectedProduct == null) {
                findViewById(R.id.button_edit_product).setEnabled(false);
                findViewById(R.id.button_delete_product).setEnabled(false);
            } else {
                findViewById(R.id.button_edit_product).setEnabled(true);
                findViewById(R.id.button_delete_product).setEnabled(true);
            }
        }
    }

    private void initializeKundeListView() {
        List<Kunde> emtyListForInitialisation = new ArrayList<>();

        productListView = findViewById(R.id.listview_products);
        productListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Kunde> productArrayAdapter = new ArrayAdapter<Kunde>(this,
                android.R.layout.simple_list_item_single_choice,emtyListForInitialisation);
        productListView.setAdapter(productArrayAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedProduct = (Product) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_product).setEnabled(true);
                findViewById(R.id.button_delete_product).setEnabled(true);
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<Product> productList = datasource.getAllProducts();
        ArrayAdapter<Product> productArrayAdapter =
                (ArrayAdapter<Product>) productListView.getAdapter();
        productArrayAdapter.clear();
        productArrayAdapter.addAll(productList);
        productArrayAdapter.notifyDataSetChanged();
    }

    public void productAnlegen(View view){
        if (selectFlag == 123) {
            selectProduct();
        }else {
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra(getString(R.string.kunde_editmode), false);
            startActivity(intent);
        }
    }

    public void productEdit(View view){
        Intent intent = new Intent(this,ProductActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_PRODUCT_ID,slectedProduct.getId());
        startActivity(intent);
    }

    public void productDelete(View view){
        Product product = datasource.getProduct(slectedProduct.getId());
        if(datasource.getAllLager_zu_Bestellungen_Product(product.getId()).size() == 0) {
            datasource.deleteProduct(product);
        }else{
            Toast.makeText(ProductListeActivity.this, "Es sind noch Bestellungen für dieses Produkt vorhanden!", Toast.LENGTH_SHORT).show();
        }
        showAllListEntries();
    }

    public void selectProduct(){
        long id = slectedProduct.getId();
        Intent intent = new Intent();
        intent.putExtra("ProductId",id);
        setResult(RESULT_OK,intent);
        finish();
    }
}
