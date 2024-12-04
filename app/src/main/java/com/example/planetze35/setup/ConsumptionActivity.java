package com.example.planetze35.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.planetze35.MainActivity;
import com.example.planetze35.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Map;

public class ConsumptionActivity extends AppCompatActivity {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(ConsumptionActivity.class);
    ListViewCF listViewCF1, listViewCF2, listViewCF3, listViewCF4;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;
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
                        listViewCF4.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Unanswered questions", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                selectedChoices.add(listViewCF1.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF2.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF3.getListView().getCheckedItemPosition());
                selectedChoices.add(listViewCF4.getListView().getCheckedItemPosition());
                //Temporarily calculate and display the total emission here
                AnnualEmissionsCalculator annualEmissionsCalculator = new AnnualEmissionsCalculator(ConsumptionActivity.this);
                annualEmissionsCalculator.readData();
                Map<String, String> emissionsByCategory = annualEmissionsCalculator.calculateEmissions(selectedChoices);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String uid = user.getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("users").child(uid);
                userRef.child("totalAnnualEmissionsByCategory").setValue(emissionsByCategory);
                userRef.child("completedSetup").setValue(true);
                //DatabaseUtils.storeOneDataField(uid, "totalAnnualEmissionsByCategory", emissionsByCategory);
                //DatabaseUtils.storeOneDataField(uid, "completedSetup",true);
                finish();
                Intent intent = new Intent(ConsumptionActivity.this, MainActivity.class);
                startActivity(intent);
                Intent intent2 = new Intent(ConsumptionActivity.this, AnnualCarbonFootprintDisplayerActivity.class);
                startActivity(intent2);
            }
        });
    }

}