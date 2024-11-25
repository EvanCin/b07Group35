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

import java.util.ArrayList;

public class FoodC2Activity extends AppCompatActivity {

    ListViewCF listViewCF1, listViewCF2, listViewCF3, listViewCF4;
    ArrayList<Integer> selectedChoices = new ArrayList<>();
    Button nextPageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_food_c2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        selectedChoices = (ArrayList<Integer>) getIntent().getSerializableExtra("selectedChoices");
        listViewCF1 = new ListViewCF(findViewById(R.id.listView1),8);
        listViewCF2 = new ListViewCF(findViewById(R.id.listView2),8);
        listViewCF3 = new ListViewCF(findViewById(R.id.listView3),8);
        listViewCF4 = new ListViewCF(findViewById(R.id.listView4),8);
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
                Intent intent = new Intent(FoodC2Activity.this, FoodC3Activity.class);
                intent.putExtra("selectedChoices", selectedChoices);
                startActivity(intent);
                finish();
            }
        });
    }
}