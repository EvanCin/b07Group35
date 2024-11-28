package com.example.planetze35.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;

public class AnnualCarbonFootprintActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    ListViewCF listViewCF;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;

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
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.countries,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        listViewCF = new ListViewCF(findViewById(R.id.listView),0);

        nextPageButton = findViewById(R.id.nextPageButton);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(listViewCF.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION) return;
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
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}