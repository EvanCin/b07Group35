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

import com.example.planetze35.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class FoodC3Activity extends AppCompatActivity {
    ListViewCF listViewCF1;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_c3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedChoices = (ArrayList<Integer>) getIntent().getSerializableExtra("selectedChoices");
        listViewCF1 = new ListViewCF(findViewById(R.id.listView1),9);
        nextPageButton = findViewById(R.id.nextPageButton);

        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listViewCF1.getListView().getCheckedItemPosition() == AdapterView.INVALID_POSITION) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Unanswered questions", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    return;
                }
                selectedChoices.add(listViewCF1.getListView().getCheckedItemPosition());
                Intent intent = new Intent(FoodC3Activity.this, HouseActivity.class);
                intent.putExtra("selectedChoices", selectedChoices);
                startActivity(intent);
                finish();
            }
        });
    }
}