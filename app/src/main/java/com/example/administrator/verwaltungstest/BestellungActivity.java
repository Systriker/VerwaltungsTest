package com.example.administrator.verwaltungstest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.TooltipCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BestellungActivity extends AppCompatActivity {

    private static final String TAG = ProductActivity.class.getSimpleName();

    private EditText editTextBestellunngsNummer, editTextKunde, editTextPreis;
    private ListView listView;
    private long id;
    private boolean editmode;
    private Datasource datasource;
    private Kunde kunde;
    private LagerZuBestellung selectedProduct;
    public static final int REQUESTCODE = 123;
    private boolean isBooked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestellung);

        datasource = new Datasource(this);

        editTextBestellunngsNummer = findViewById(R.id.edit_BestellunngsNummer);
        editTextKunde = findViewById(R.id.edit_Kunde);
        editTextPreis = findViewById(R.id.editBestellungPreis);
        listView = findViewById(R.id.listview_bestellung_products);

        editmode = getIntent().getBooleanExtra(getString(R.string.kunde_editmode),false);
        id = getIntent().getLongExtra(DbHelper.COLUMN_BESTELLUNG_ID,0L);
        editTextPreis.setEnabled(false);

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
        if (editmode) {
            showAllListEntries();
        }
    }

    private void activateButtons() {
        Button saveButton = findViewById(R.id.button_save_bestellung);

        TooltipCompat.setTooltipText(saveButton,getString(R.string.hint_save));
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String KundeName = editTextKunde.getText().toString();
                String BestellungNummer = editTextBestellunngsNummer.getText().toString();
                Long BestellungNummerLong;

                if (!BestellungNummer.equals("")){
                    BestellungNummerLong = Long.parseLong(BestellungNummer);
                }else {
                    BestellungNummerLong = 1L;
                }

                if (TextUtils.isEmpty(KundeName)){
                    editTextKunde.setError(getString(R.string.editText_errorMessage));
                    editTextKunde.requestFocus();
                    return;
                }

                if (editmode){
                    datasource.updateBestellung(BestellungNummerLong, (int)kunde.getId(),0);
                }else {
                    datasource.createBestellung(BestellungNummerLong, (int)kunde.getId(),0);
                }
                finish();
            }
        });

        Button selectKundeButton = findViewById(R.id.button_select_kunde);
        selectKundeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),KundeListeActivity.class);
                intent.putExtra("selectFlag",123);
                startActivityForResult(intent, REQUESTCODE);
            }
        });

        Button addProductButton = findViewById(R.id.button_bestellung_product_add);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),LagerZuBestellungActivity.class);
                intent.putExtra(getString(R.string.kunde_editmode),false);
                intent.putExtra("Bestellung",id);
                startActivity(intent);
                showAllListEntries();
            }
        });

        Button deleteProductButton = findViewById(R.id.button_bestellung_product_delete);
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datasource.deleteLager_zu_Bestellung(selectedProduct);
                showAllListEntries();
            }
        });

        Button deleteButton = findViewById(R.id.button_delete_bestellung);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bestellung bestellung = datasource.getBestellung(id);
                datasource.deleteBestellung(bestellung);
                finish();
            }
        });

        Button buchenButton = findViewById(R.id.buttonBuchen);
        buchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != 0L){
                    Boolean canBook = true;
                    List<LagerZuBestellung> productList = datasource.getAllLager_zu_Bestellungen(id);
                    for (LagerZuBestellung lzb: productList){
                        if (!(lzb.getQuantity()<=lzb.getProduct().getQuantity())){
                            canBook = false;
                        }else {
                            Toast.makeText(BestellungActivity.this,
                                    "Es sind nicht genügend Produkte vom Typ:" +
                                    lzb.getProduct().getName() +
                                    "vorhanden!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (canBook) {
                        for (LagerZuBestellung lzb: productList){
                            datasource.updateProduct(lzb.getProduct().getId(),lzb.getProduct().getName(),
                                    lzb.getProduct().getQuantity() - lzb.getQuantity(),
                                    lzb.getProduct().getPreis());
                        }
                        datasource.updateBestellung(id, (int) kunde.getId(), 1);
                        finish();
                    }
                }
            }
        });
    }

    private void fillPage(){

        editTextBestellunngsNummer.setEnabled(false);
        findViewById(R.id.button_bestellung_product_delete).setEnabled(false);
        findViewById(R.id.button_bestellung_product_add).setEnabled(false);
        editTextKunde.setEnabled(false);
        getSupportActionBar().setTitle("Neue Bestellung");

        if (id != 0L){
            Bestellung bestellung = datasource.getBestellung(id);
            getSupportActionBar().setTitle("Bestellung: " + id);
            editTextBestellunngsNummer.setText(String.valueOf(id));
            findViewById(R.id.button_bestellung_product_add).setEnabled(true);
            if (editmode) {
                kunde = bestellung.getKunde();
                editTextKunde.setText(kunde.getName());
                initializeBestellungListView();
                isBooked = bestellung.isBooked();
                if(isBooked) {
                    findViewById(R.id.button_bestellung_product_add).setEnabled(false);
                    findViewById(R.id.button_select_kunde).setEnabled(false);
                    findViewById(R.id.buttonBuchen).setEnabled(false);
                    findViewById(R.id.buttonBuchen).setEnabled(false);
                }
            }
        }
    }

    private void initializeBestellungListView() {
        List<LagerZuBestellung> emtyListForInitialisation = new ArrayList<>();

        listView = findViewById(R.id.listview_bestellung_products);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<LagerZuBestellung> productArrayAdapter = new ArrayAdapter<LagerZuBestellung>(this,
                android.R.layout.simple_list_item_single_choice,emtyListForInitialisation);
        listView.setAdapter(productArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProduct = (LagerZuBestellung) adapterView.getItemAtPosition(i);
                if(!isBooked) {
                    findViewById(R.id.button_bestellung_product_delete).setEnabled(true);
                }
                showAllListEntries();
            }
        });
    }

    private void showAllListEntries() {
        List<LagerZuBestellung> productList = datasource.getAllLager_zu_Bestellungen(id);
        ArrayAdapter<LagerZuBestellung> productArrayAdapter =
                (ArrayAdapter<LagerZuBestellung>) listView.getAdapter();
        productArrayAdapter.clear();
        productArrayAdapter.addAll(productList);
        productArrayAdapter.notifyDataSetChanged();
        double preis = 0;
        for (LagerZuBestellung lzb : productList){
            preis += (lzb.getProduct().getPreis() * lzb.getQuantity());
        }
        editTextPreis.setText(String.valueOf(preis) + "€");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        long id = data.getLongExtra("KundeId",0);
        datasource.open();
        kunde = datasource.getKunde(id);
        editTextKunde.setText(kunde.getName());
    }
}
