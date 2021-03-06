package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

//Klasse zur anzeige und auswahl von Produkten in einer Liste
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

        //wenn die Liste zur auswahl eines Produktes genutzt wird
        selectFlag = getIntent().getIntExtra("selectFlag",0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen");
        slectedProduct = null;
        datasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Die Datenquelle wird geöffnet");
        datasource.open();
        Log.d(TAG, "folgende Einträge sind in der DB vorhanden: ");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.titleProdukte));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_sort_by_size);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        showAllListEntries();
        //wenn die Liste zur auswahl genutzt wird werden die editier und löschen Buttons ausgeblendet
        //und der hinzufügen Button als bestätigungs Button verwendet
        if (selectFlag == 123){
            findViewById(R.id.button_edit_product).setEnabled(false);
            findViewById(R.id.button_delete_product).setEnabled(false);
            findViewById(R.id.button_edit_product).setVisibility(Button.INVISIBLE);
            findViewById(R.id.button_delete_product).setVisibility(Button.INVISIBLE);
            ((Button)findViewById(R.id.button_add_product)).setText(R.string.button_ok);
        }else {
            //aktiviere bearbeiten und löschen wenn ein bereits vorhandes Produkt ausgewählt wurde
            if (slectedProduct == null) {
                findViewById(R.id.button_edit_product).setEnabled(false);
                findViewById(R.id.button_delete_product).setEnabled(false);
                (findViewById(R.id.button_edit_product)).getBackground()
                        .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                (findViewById(R.id.button_delete_product)).getBackground()
                        .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
            } else {
                findViewById(R.id.button_edit_product).setEnabled(true);
                findViewById(R.id.button_delete_product).setEnabled(true);
                (findViewById(R.id.button_edit_product)).getBackground().setColorFilter(null);
                (findViewById(R.id.button_delete_product)).getBackground().setColorFilter(null);
            }
        }
    }

    private void initializeKundeListView() {
        List<Kunde> emtyListForInitialisation = new ArrayList<>();

        productListView = findViewById(R.id.listview_products);
        productListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<Kunde> productArrayAdapter = new ArrayAdapter<>(this,
               R.layout.list_item_basis,emtyListForInitialisation);
        productListView.setAdapter(productArrayAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                slectedProduct = (Product) adapterView.getItemAtPosition(i);
                findViewById(R.id.button_edit_product).setEnabled(true);
                findViewById(R.id.button_delete_product).setEnabled(true);
                (findViewById(R.id.button_edit_product)).getBackground().setColorFilter(null);
                (findViewById(R.id.button_delete_product)).getBackground().setColorFilter(null);
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
        //wenn die Liste zur auswahl genutzt wird, wird der
        //hinzufügen Button als bestätigungs Button verwendet
        if (selectFlag == 123) {
            selectProduct();
        }else {
            Intent intent = new Intent(this, ProductActivity.class);
            intent.putExtra(getString(R.string.kunde_editmode), false);
            startActivity(intent);
        }
    }

    //öfnnen der ProduktActivity im editierbaren Modus
    public void productEdit(View view){
        Intent intent = new Intent(this,ProductActivity.class);
        intent.putExtra(getString(R.string.kunde_editmode),true);
        intent.putExtra(DbHelper.COLUMN_PRODUCT_ID,slectedProduct.getId());
        startActivity(intent);
    }

    public void productDelete(View view){
        Product product = datasource.getProduct(slectedProduct.getId());
        //Prüfung auf noch vorhandende Bestellungen zu diesem Produkt
        if(datasource.getAllLager_zu_Bestellungen_Product(product.getId()).size() == 0) {
            datasource.deleteProduct(product);
        }else{
            Toast.makeText(ProductListeActivity.this, getResources().getString(R.string.ProduktDeleteError), Toast.LENGTH_SHORT).show();
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
