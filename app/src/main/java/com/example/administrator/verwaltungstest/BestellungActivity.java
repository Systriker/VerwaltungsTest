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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

//Klasse für die genauere Ansicht/das Bearbeiten einer Bestellung
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

        //beim anlegen kann noch nicht gelöscht werden
        if (!editmode){
            findViewById(R.id.button_delete_bestellung).setEnabled(false);
            (findViewById(R.id.button_delete_bestellung)).getBackground()
                    .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }

        activateButtons();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Die Datenquelle wird geschlossen");
        selectedProduct = null;
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
                    if (datasource.getBestellung(id).isBooked()) {
                        datasource.updateBestellung(BestellungNummerLong, (int) kunde.getId(), 1);
                    }else {
                        datasource.updateBestellung(BestellungNummerLong, (int) kunde.getId(), 0);
                    }
                }else {
                    datasource.createBestellung((int)kunde.getId());
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

        ImageButton addProductButton = findViewById(R.id.button_bestellung_product_add);
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

        ImageButton deleteProductButton = findViewById(R.id.button_bestellung_product_delete);
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
                if(datasource.getAllLager_zu_Bestellungen(bestellung.getId()).size() == 0){
                    datasource.deleteBestellung(bestellung);
                }else{
                    Toast.makeText(BestellungActivity.this,
                            getResources().getString(R.string.BestellungDeleteError), Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        Button buchenButton = findViewById(R.id.buttonBuchen);
        buchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != 0L){
                    List<LagerZuBestellung> productList = datasource.getAllLager_zu_Bestellungen(id);
                    for (LagerZuBestellung lzb: productList){
                        if (!(lzb.getQuantity()<=lzb.getProduct().getQuantity())){
                            Toast.makeText(BestellungActivity.this,
                                    String.format(getResources().getString(R.string.BestellungEditError),
                                            lzb.getProduct().getName()), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    for (LagerZuBestellung lzb: productList){
                        datasource.updateProduct(lzb.getProduct().getId(),lzb.getProduct().getName(),
                                lzb.getProduct().getQuantity() - lzb.getQuantity(),
                                lzb.getProduct().getPreis());
                    }
                    datasource.updateBestellung(id, (int) kunde.getId(), 1);
                    finish();
                }
            }
        });
    }

    private void fillPage(){

        editTextBestellunngsNummer.setEnabled(false);
        findViewById(R.id.button_bestellung_product_delete).setEnabled(false);
        (findViewById(R.id.button_bestellung_product_delete)).getBackground()
                .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        findViewById(R.id.button_bestellung_product_add).setEnabled(false);
        (findViewById(R.id.button_bestellung_product_add)).getBackground()
                .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        editTextKunde.setEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getResources().getString(R.string.neueBestellung));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(android.R.drawable.ic_menu_agenda);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        findViewById(R.id.buttonBuchen).setEnabled(false);
        (findViewById(R.id.buttonBuchen)).getBackground()
                .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);

        if (id != 0L){
            Bestellung bestellung = datasource.getBestellung(id);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(getResources().getString(R.string.titleBestellung) + ": " + id);
            }
            editTextBestellunngsNummer.setText(String.valueOf(id));
            findViewById(R.id.button_bestellung_product_add).setEnabled(true);
            (findViewById(R.id.button_bestellung_product_add)).getBackground().setColorFilter(null);
            if (editmode) {
                kunde = bestellung.getKunde();
                editTextKunde.setText(kunde.getName());
                initializeBestellungListView();
                isBooked = bestellung.isBooked();
                findViewById(R.id.buttonBuchen).setEnabled(true);
                (findViewById(R.id.buttonBuchen)).getBackground().setColorFilter(null);
                // Sperre Bearbeitung wenn Bestellung bereits gebucht wurde
                if(isBooked) {
                    findViewById(R.id.button_bestellung_product_add).setEnabled(false);
                    (findViewById(R.id.button_bestellung_product_add)).getBackground()
                            .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    findViewById(R.id.button_select_kunde).setEnabled(false);
                    (findViewById(R.id.button_select_kunde)).getBackground()
                            .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                    findViewById(R.id.buttonBuchen).setEnabled(false);
                    (findViewById(R.id.buttonBuchen)).getBackground()
                            .setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                }
            }
        }
    }

    private void initializeBestellungListView() {
        List<LagerZuBestellung> emtyListForInitialisation = new ArrayList<>();

        listView = findViewById(R.id.listview_bestellung_products);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<LagerZuBestellung> productArrayAdapter = new ArrayAdapter<>(this,
                R.layout.list_item_basis,emtyListForInitialisation);
        listView.setAdapter(productArrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProduct = (LagerZuBestellung) adapterView.getItemAtPosition(i);
                if(!isBooked) {
                    findViewById(R.id.button_bestellung_product_delete).setEnabled(true);
                    (findViewById(R.id.button_bestellung_product_delete)).getBackground().setColorFilter(null);
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
        //Berechne Gesamtpreis der Bestellung
        for (LagerZuBestellung lzb : productList){
            preis += (lzb.getProduct().getPreis() * lzb.getQuantity());
        }
        String text = String.valueOf(preis) + "€";
        editTextPreis.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!(resultCode == 0)) {
            long id = data.getLongExtra("KundeId", 0);
            if (!(id == 0)) {
                datasource.open();
                kunde = datasource.getKunde(id);
                editTextKunde.setText(kunde.getName());
            }
        }
    }
}
