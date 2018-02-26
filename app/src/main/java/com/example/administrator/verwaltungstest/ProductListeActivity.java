package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ProductListeActivity extends AppCompatActivity {

    private static final String TAG = ProductListeActivity.class.getSimpleName();

    private ListView productListView;
    private Datasource datasource;
    private Product slectedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_liste);
        initializeKundeListView();
        datasource = new Datasource(this);
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
        if (slectedProduct == null){
            findViewById(R.id.button_edit_product).setEnabled(false);
            findViewById(R.id.button_delete_product).setEnabled(false);
        }else {
            findViewById(R.id.button_edit_product).setEnabled(true);
            findViewById(R.id.button_delete_product).setEnabled(true);
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
        Intent intent = new Intent(this,ProductActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),false);
        startActivity(intent);
    }

    public void productEdit(View view){
        Intent intent = new Intent(this,ProductActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_PRODUCT_ID,slectedProduct.getId());
        startActivity(intent);
    }

    public void productDelete(View view){
        Product product = datasource.getProduct(slectedProduct.getId());
        datasource.deleteProduct(product);
        showAllListEntries();
    }
}
