package com.example.planetze35.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ConsumptionActivity extends AppCompatActivity {

    ListViewCF listViewCF1, listViewCF2, listViewCF3, listViewCF4;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_consumption);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedChoices = (ArrayList<Integer>) getIntent().getSerializableExtra("selectedChoices");
        listViewCF1 = new ListViewCF(findViewById(R.id.listView1),17);
        listViewCF2 = new ListViewCF(findViewById(R.id.listView2),18);
        listViewCF3 = new ListViewCF(findViewById(R.id.listView3),19);
        listViewCF4 = new ListViewCF(findViewById(R.id.listView4),20);
        nextPageButton = findViewById(R.id.nextPageButton);

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listViewCF1.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION ||
                        listViewCF2.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION ||
                        listViewCF3.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION ||
                        listViewCF4.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION) return;
                selectedChoices.add(listViewCF1.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF2.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF3.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF4.getListView().getCheckedItemPosition());

                //Temporarily calculate and display the total emission here
                AnnualEmissionsCalculator annualEmissionsCalculator = new AnnualEmissionsCalculator(ConsumptionActivity.this);
                annualEmissionsCalculator.readData();
                double totalEmissions = annualEmissionsCalculator.calculateEmissions(selectedChoices);
                Log.i("Total Emissions", Double.toString(totalEmissions));

                //Temporarily store data to firebase using dummy user
                mDatabase = FirebaseDatabase.getInstance().getReference("users");
                mDatabase.child("Bob/totalEmissions").setValue(totalEmissions);
                mDatabase.child("Bob/completedSetup").setValue(true);
                finish();
            }
        });
    }

}