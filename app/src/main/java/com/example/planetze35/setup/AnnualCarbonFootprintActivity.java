package com.example.planetze35.setup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AnnualCarbonFootprintActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    ListViewCF listViewCF;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;
    ArrayList<String> countries;
    String[] nonCountries = {"Africa","Asia","Asia (excl. China and India)","Australia","Europe"
                            ,"Europe (excl. EU-27)","Europe (excl. EU-28)","European Union (27)"
                            ,"European Union (28)","High-income countries","Low-income countries"
                            ,"Lower-middle-income countries","North America","North America (excl. USA)","Oceania"
                            ,"South America","Upper-middle-income countries","World"};
    final String DEFAULT_CHOICE = "---Select Country---";
    String selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_annual_carbon_footprint);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        readCSV();
        //Spinner for the countries
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,countries);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

        listViewCF = new ListViewCF(findViewById(R.id.listView),0);
        nextPageButton = findViewById(R.id.nextPageButton);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(listViewCF.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Unanswered questions", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                if(selectedCountry == null || selectedCountry.equals(DEFAULT_CHOICE)) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Country not selected", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                selectedChoices.add(listViewCF.getListView().getCheckedItemPosition());
                if(selectedChoices.get(0) == 0) {
                    intent = new Intent(AnnualCarbonFootprintActivity.this, TransportationC1Activity.class);
                } else {
                    intent = new Intent(AnnualCarbonFootprintActivity.this, TransportationC2Activity.class);
                    //Skip questions 2 and 3
                    selectedChoices.add(-1);
                    selectedChoices.add(-1);
                }
                intent.putExtra("selectedChoices", selectedChoices);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String uid = user.getUid();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users").child(uid);
                userRef.child("country").setValue(selectedCountry);
                startActivity(intent);
                finish();
            }
        });
    }
    public void readCSV() {
        countries = new ArrayList<>();
        countries.add(DEFAULT_CHOICE);
        InputStream stream = AnnualCarbonFootprintActivity.this.getResources()
                .openRawResource(R.raw.global_averages);
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            String line = br.readLine();
            line = br.readLine();
            readLoop: while(line != null) {
                String country = line.split(",")[0];
                for(String str:nonCountries) {
                    if(str.equals(country)) {
                        line = br.readLine();
                        continue readLoop;
                    }
                }
                countries.add(line.split(",")[0]);
                line = br.readLine();
            }
        } catch(IOException ioException) {
            Log.d("AnnualCarbonFootprintActivity", "IOException");
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        selectedCountry = countries.get(pos);
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}