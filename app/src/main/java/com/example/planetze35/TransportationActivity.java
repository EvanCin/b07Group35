package com.example.planetze35;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TransportationActivity extends AppCompatActivity {

    private Button buttonPersonalVehicle, buttonPublicTransport, buttonCycling, buttonFlight, Done_button;
    private LinearLayout questionsPersonVehicle, questionsTransportation, questionsCyclingWalking, questionsFlight;
    private Spinner spinnerPersonalVehicle, spinnerTransportation;
    private EditText driveVehicle, edtTimePublicTransport, walkingDistance, numFlights, typeFlights;
    private Button addPersonalVehicleButton, addPublicTransportButton, addCyclingButton, addFlightButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transportation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.transport), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize components
        initUIComponents();

        // Initialize ScrollView for scrolling down when new content appears
        ScrollView scrollView = findViewById(R.id.transport);

        // Set up onClick listeners for each transportation option
        setOnClickListeners(scrollView);
    }
    // Method to initialize all the UI components
    private void initUIComponents() {
        // Personal Vehicle UI components
        buttonPersonalVehicle = findViewById(R.id.button_personal_vehicle);
        questionsPersonVehicle = findViewById(R.id.questionsPersonVehicle);
        spinnerPersonalVehicle = findViewById(R.id.spinnerPersonalVehicle);
        driveVehicle = findViewById(R.id.driveVehicle);
        addPersonalVehicleButton = findViewById(R.id.add_button_personalVehicle);

        // Public Transport UI components
        buttonPublicTransport = findViewById(R.id.button_publicTransport);
        questionsTransportation = findViewById(R.id.questionsTransportation);
        spinnerTransportation = findViewById(R.id.spinnerTransportation);
        edtTimePublicTransport = findViewById(R.id.edtTimePublicTransport);
        addPublicTransportButton = findViewById(R.id.add_button_publicTransport);

        // Cycling/Walking UI components
        buttonCycling = findViewById(R.id.buttonCycling);
        questionsCyclingWalking = findViewById(R.id.questionsCyclingWalking);
        walkingDistance = findViewById(R.id.walkingDistance);
        addCyclingButton = findViewById(R.id.add_button_Cycling);

        // Flight UI components
        buttonFlight = findViewById(R.id.buttonFlight);
        questionsFlight = findViewById(R.id.questionsFlight);
        numFlights = findViewById(R.id.num_Flights);
        typeFlights = findViewById(R.id.typeFlights);
        addFlightButton = findViewById(R.id.add_button_Flight);
        Done_button = findViewById(R.id.done_button);

        // Set up Spinners
        setUpSpinner(spinnerPersonalVehicle, R.array.vehicle_types);
        setUpSpinner(spinnerTransportation, R.array.public_transport_options);
    }
    // Helper method to set up spinner with array resource
    private void setUpSpinner(Spinner spinner, int arrayResourceId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void setOnClickListeners(ScrollView scrollView) {
        // Personal Vehicle Button Action
        buttonPersonalVehicle.setOnClickListener(v -> toggleVisibility(questionsPersonVehicle, scrollView));

        // Public Transport Button Action
        buttonPublicTransport.setOnClickListener(v -> toggleVisibility(questionsTransportation, scrollView));

        // Cycling/Walking Button Action
        buttonCycling.setOnClickListener(v -> toggleVisibility(questionsCyclingWalking, scrollView));

        // Flight Button Action
        buttonFlight.setOnClickListener(v -> toggleVisibility(questionsFlight, scrollView));

        // Done Button Action
        Done_button.setOnClickListener(v -> scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN)));
        Done_button.setOnClickListener(v -> {
            // Redirect to the main page
            Intent intent = new Intent(TransportationActivity.this, EcoTrackerDailyActivityHub.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Add Button Actions
        addPersonalVehicleButton.setOnClickListener(v -> handleAddPersonalVehicle());
        addPublicTransportButton.setOnClickListener(v -> handleAddPublicTransport());
        addCyclingButton.setOnClickListener(v -> handleAddCycling());
        addFlightButton.setOnClickListener(v -> handleAddFlight());
    }
    // Helper method to toggle visibility of transportation question sections
    private void toggleVisibility(LinearLayout selectedLayout, ScrollView scrollView) {
        questionsPersonVehicle.setVisibility(View.GONE);
        questionsTransportation.setVisibility(View.GONE);
        questionsCyclingWalking.setVisibility(View.GONE);
        questionsFlight.setVisibility(View.GONE);

        selectedLayout.setVisibility(View.VISIBLE);

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }
    // Helper method to handle adding Personal Vehicle data
    private void handleAddPersonalVehicle() {
        String vehicleType = spinnerPersonalVehicle.getSelectedItem().toString();
        String distance = driveVehicle.getText().toString();

        if (!distance.isEmpty()) {
            showToast("Added: " + vehicleType + " - " + distance);
            driveVehicle.setText("");
        } else {
            showToast("Please enter the distance");
        }
    }
    // Helper method to handle adding Public Transport data
    private void handleAddPublicTransport() {
        String transportType = spinnerTransportation.getSelectedItem().toString();
        String timeSpent = edtTimePublicTransport.getText().toString();

        if (!timeSpent.isEmpty()) {
            showToast("Added: " + transportType + " - " + timeSpent + " hours");
            edtTimePublicTransport.setText("");
        } else {
            showToast("Please enter the time spent");
        }
    }
    // Helper method to handle adding Cycling data
    private void handleAddCycling() {
        String distanceWalked = walkingDistance.getText().toString();

        if (!distanceWalked.isEmpty()) {
            showToast("Added: " + distanceWalked);
            walkingDistance.setText("");
        } else {
            showToast("Please enter the distance");
        }
    }
    // Helper method to handle adding Flight data
    private void handleAddFlight() {
        String numOfFlights = numFlights.getText().toString();
        String flightType = typeFlights.getText().toString();

        if (!numOfFlights.isEmpty() && !flightType.isEmpty()) {
            showToast("Added: " + numOfFlights + " flights - " + flightType);
            numFlights.setText("");  // Clear the number of Flights field
            typeFlights.setText("");
        } else {
            showToast("Please enter the details");
        }
    }
    // Helper method to show a Toast message
    private void showToast(String message) {
        Toast.makeText(TransportationActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}