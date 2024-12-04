package com.example.planetze35.EcoTrackerAllParts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.example.planetze35.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Objects;

public class TransportationActivity extends AppCompatActivity {
    private DatabaseReference databaseRef;
    private DatabaseReference dailyActivitiesRef;
    private String selectedDate;
    private String userId;
    private Button buttonPersonalVehicle, buttonPublicTransport, buttonCycling, buttonFlight, Done_button;
    private LinearLayout questionsPersonVehicle, questionsTransportation, questionsCyclingWalking, questionsFlight;
    private Spinner spinnerPersonalVehicle, spinnerTransportation, spinnerTypeFlights;
    private EditText driveVehicle, edtTimePublicTransport, walkingDistance, numFlights;
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
        // Initialize Firebase reference
        databaseRef = FirebaseDatabase.getInstance().getReference();
        selectedDate = getIntent().getStringExtra("selectedDate");
        userId = getUserId();
        if (userId == null) {
            // Handle the case where no user is logged in
            showToast("You must be logged in to add transportation data.");
            return;
        }
        dailyActivitiesRef = databaseRef .child("users")
                .child(userId)  // Use the logged-in user's UID
                .child("DailyActivities")
                .child(selectedDate);
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
        spinnerTypeFlights = findViewById(R.id.spinnerTypeFlights);
        addFlightButton = findViewById(R.id.add_button_Flight);
        Done_button = findViewById(R.id.done_button);

        // Set up Spinners
        setUpSpinner(spinnerPersonalVehicle, R.array.vehicle_types);
        setUpSpinner(spinnerTransportation, R.array.public_transport_options);
        setUpSpinner(spinnerTypeFlights, R.array.flight_types);
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
    // Add Personal Vehicle Data
    private void handleAddPersonalVehicle() {
        String distanceText = driveVehicle.getText().toString();
        String vehicleType = spinnerPersonalVehicle.getSelectedItem().toString();

        if (!distanceText.isEmpty() && !vehicleType.isEmpty()) {
            double distance = Double.parseDouble(distanceText);
            DrivePersonalVehicle personalVehicle = new DrivePersonalVehicle(distance, vehicleType);
            // Prepare the data to be stored in the database
            Map<String, Object> vehicleData = personalVehicle.toMap();
            // Store data in Firebase
            updateOrAddTransportData(userId, selectedDate, "drivePersonalVehicle", vehicleType, vehicleData);
            EmissionsHelper.updateTotalEmissions(dailyActivitiesRef);
            // Clear input fields
            driveVehicle.setText("");
        } else {
            showToast("Please enter the distance and select a vehicle type");
        }
    }
    // Add Public Transport Data
    private void handleAddPublicTransport() {
        String timeText = edtTimePublicTransport.getText().toString();
        String transportType = spinnerTransportation.getSelectedItem().toString();

        if (!timeText.isEmpty() && !transportType.isEmpty()) {
            double timeSpent = Double.parseDouble(timeText);
            TakePublicTransportation publicTransport = new TakePublicTransportation(timeSpent, transportType);
            // Prepare data to be stored
            Map<String, Object> transportData = publicTransport.toMap();
            // Store data in Firebase
            updateOrAddTransportData(userId, selectedDate, "takePublicTransportation", transportType, transportData);
            EmissionsHelper.updateTotalEmissions(dailyActivitiesRef);
            // Clear input fields
            edtTimePublicTransport.setText("");
        } else {
            showToast("Please enter the time spent and select a transport mode");
        }
    }

    // Add Cycling Data
    private void handleAddCycling() {
        String distanceText = walkingDistance.getText().toString();

        if (!distanceText.isEmpty()) {
            double distance = Double.parseDouble(distanceText);
            CyclingOrWalking cycling = new CyclingOrWalking(distance);
            // Prepare data to be stored
            Map<String, Object> cyclingData = cycling.toMap();
            // Store data in Firebase
            updateOrAddTransportData(userId, selectedDate, "cyclingOrWalking", null, cyclingData);
            EmissionsHelper.updateTotalEmissions(dailyActivitiesRef);
            // Clear input fields
            walkingDistance.setText("");
        } else {
            showToast("Please enter the cycling distance");
        }
    }

    // Add Flight Data
    private void handleAddFlight() {
        String flightCountText = numFlights.getText().toString();
        String flightType = spinnerTypeFlights.getSelectedItem().toString();

        if (!flightCountText.isEmpty() && !flightType.isEmpty()) {
            int flightCount = Integer.parseInt(flightCountText);
            Flight flight = new Flight(flightCount, flightType);
            // Prepare Data to be stored
            Map<String, Object> flightData = flight.toMap();
            // Store data in Firebase
            updateOrAddTransportData(userId, selectedDate, "flight", flightType, flightData);
            EmissionsHelper.updateTotalEmissions(dailyActivitiesRef);
            // Clear input fields
            numFlights.setText("");
        } else {
            showToast("Please enter the flight count and select a flight type");
        }
    }
    // Helper method to update or add data in Firebase
    private void updateOrAddTransportData(String userId, String date, String transportType, String subType, Map<String, Object> transportData) {
        if (subType != null) {
            // Store data under the specific transportation subtype (e.g., gasoline, bus)
            databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                    .child("transportation").child(transportType).child(subType)
                    .setValue(transportData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Added " + transportType + " data for " + date);
                        } else {
                            showToast("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        } else {
            // For cycling/walking data (no subtype)
            databaseRef.child("users").child(userId).child("DailyActivities").child(date)
                    .child("transportation").child(transportType)
                    .setValue(transportData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showToast("Added " + transportType + " data for " + date);
                        } else {
                            showToast("Error: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }
    // Helper function to get user ID (replace with Firebase Auth method)
    private String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If a user is logged in, return their UID
            return currentUser.getUid();
        } else {
            // Handle case when no user is logged in
            Log.e("TransportationActivity", "No user is logged in");
            return null;  // or throw an exception, or handle as needed
        }
    }
    // Helper method to show a Toast message
    private void showToast(String message) {
        Toast.makeText(TransportationActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}