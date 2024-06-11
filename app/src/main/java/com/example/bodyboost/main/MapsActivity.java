package com.example.bodyboost.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.bodyboost.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    ImageView ivBack;
    private FusedLocationProviderClient fusedLocationClient;
    private PlacesClient placesClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));

        ivBack = findViewById(R.id.ivBack);
        // Initialize Places API
        Places.initialize(getApplicationContext(), "AIzaSyD9-7VF_J-YFK2g8_UethYoeR73R_pNGtY");
        placesClient = Places.createClient(this);

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Setup the search bar
        setupSearchBar();

    }

    private void setupSearchBar() {
        // Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(List.of(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the selection of a place
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Show dialog with options
                showLocationDialog(place.getName(), place.getLatLng());
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                Toast.makeText(MapsActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLocationDialog(String locationName, LatLng locationLatLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selected Location")
                .setMessage("Location: " + locationName)
                .setPositiveButton("See Location on Map", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Open Google Maps with the selected location
                        openGoogleMaps(locationLatLng);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, start location updates
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Move camera to current location
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

                        // Add a marker at the current location
                        mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("You are here"));

                        // Fetch nearby gyms and add markers
                        fetchNearbyGyms(currentLocation);
                    }
                });
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchNearbyGyms(LatLng location) {

        // Create a new Places API client
        PlacesClient placesClient = Places.createClient(this);

        // Create a Nearby Search request
        String locationString = location.latitude + "," + location.longitude;
        String radius = "50000"; // 5000 meters (adjust as needed)

        String type = "gym"; // Type for gyms
        String keyword = "gym"; // Keyword for gyms

        String apiKey = "AIzaSyD9-7VF_J-YFK2g8_UethYoeR73R_pNGtY";

        String url = String.format("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&type=%s&keyword=%s&key=%s",
                locationString, radius, type, keyword, apiKey);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // Make an HTTP request and parse the response
                    URL apiUrl = new URL(url);
                    HttpURLConnection urlConnection = (HttpURLConnection) apiUrl.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        // Parse the JSON response and add markers for each gym
                        parseNearbyGymsResponse(in);
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Handle any post-execution tasks if needed
            }
        }.execute();
    }

    private void parseNearbyGymsResponse(InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder responseStringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                responseStringBuilder.append(line);
            }

            // Parse the JSON response
            JSONObject response = new JSONObject(responseStringBuilder.toString());

            // Check if the response contains results
            if (response.has("results")) {
                JSONArray resultsArray = response.getJSONArray("results");

                // Limit the number of gyms to display
                int numberOfGyms = Math.min(resultsArray.length(), 5);

                // Create LatLngBounds to include all markers
                LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

                for (int i = 0; i < numberOfGyms; i++) {
                    JSONObject gymObject = resultsArray.getJSONObject(i);
                    JSONObject locationObject = gymObject.getJSONObject("geometry").getJSONObject("location");

                    double lat = locationObject.getDouble("lat");
                    double lng = locationObject.getDouble("lng");
                    LatLng gymLocation = new LatLng(lat, lng);

                    // Get the address for the location
                    String address = getAddressFromLocation(gymLocation);

                    // Add marker for each gym with address
                    runOnUiThread(() -> {
                        try {
                            Marker marker = mMap.addMarker(new MarkerOptions()
                                    .position(gymLocation)
                                    .title(gymObject.getString("name"))
                                    .snippet(address) // Set the address as snippet
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                            // Include the marker in the bounds
                            boundsBuilder.include(gymLocation);

                            // Set a click listener for the marker
                            mMap.setOnMarkerClickListener(clickedMarker -> {
                                if (clickedMarker.equals(marker)) {
                                    // Open Google Maps with the specific location when marker is clicked
                                    openGoogleMaps(gymLocation);
                                    return true;
                                }
                                return false;
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

                // Build the LatLngBounds from the markers' positions
                LatLngBounds bounds = boundsBuilder.build();

                // Calculate a reasonable zoom level based on the distance between markers
                int padding = getMapPadding(); // Set padding as needed (in pixels)
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to calculate padding for the newLatLngBounds
    private int getMapPadding() {
        // Set padding as needed (in pixels)
        return 100;
    }


    // Helper method to get the address from the LatLng location
    private String getAddressFromLocation(LatLng location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Helper method to open Google Maps with the specific location
    private void openGoogleMaps(LatLng location) {
        Uri gmmIntentUri = Uri.parse("geo:" + location.latitude + "," + location.longitude + "?q=" + location.latitude + "," + location.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                if (mMap != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}