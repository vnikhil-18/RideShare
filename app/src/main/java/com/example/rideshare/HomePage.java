package com.example.rideshare;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class HomePage extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{
    //this class is used to create the Home Page
    DrawerLayout drawerLayout; //drawer layout is added to get the navigation view from the Home Page
    NavigationView navigationView; //navigation view "three bars on the top left side" in the Home Page
    Toolbar toolbar;   //tool bar section in the Home Page
    private GoogleMap map; //map section in the Home Page
    private Boolean mLocationPermissionsGranted = false; //this variable is used to check if the location permission is granted or not
    Place src,dst; //this variable is used to store the source and destination location
    Button button;
    private FusedLocationProviderClient fusedLocationProviderClient; //fused location provider client is used to get the current location of the user
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            //this method is used to set the boundaries of the map
            new LatLng(-40, -168), new LatLng(71, 136));
    private FirebaseAuth auth; //auth is used to get the current user
    private FirebaseFirestore fstore; //firestore is used to store the data in the database

    static List<Polyline> v; //polyline is used to draw the route between the source and destination

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this method is used to create the Home Page
        v=new LinkedList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar); //this method is used to set the toolbar in the Home Page
        navigationView.bringToFront();//this method is used to bring the navigation view to the front
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle); //this method is used to add the toggle to the drawer layout
        toggle.syncState(); //this method is used to sync the toggle with the drawer layout
        navigationView.setNavigationItemSelectedListener(this);//this method is used to set the navigation view listener
        navigationView.setCheckedItem(R.id.nav_home);//this method is used to set the home page as the default page
        getLocationPermission();//this method is used to get the location permission from the user

    }

    private void init() {
        //this method is used to initialize the map and the autocomplete fragments
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(HomePage.this);
        String apiKey = getString(R.string.my_api_key); //this variable is used to store the api key
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apiKey);
        }
        PlacesClient placesClient= Places.createClient(this); //this method is used to create the places client
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        AutocompleteSupportFragment autocompleteFragment2 = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment2);
        autocompleteFragment.setCountries("IN");
        autocompleteFragment2.setCountries("IN");
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        autocompleteFragment2.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            //autocomplete fragment is used to get the source input from the user
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //when the source is selected the geoLocate method is called
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                src=place;
                geoLocate();
            }
            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        autocompleteFragment2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            //autocomplete fragment2 is used to get the destination input from the user
            @Override
            public void onError(@androidx.annotation.NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }

            @Override
            public void onPlaceSelected(@androidx.annotation.NonNull Place place) {
                dst=place;
            } //when the destination is selected the getDirection method is called
        });

        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDirection();
            } //when the button is clicked the getDirection method is called
        });

    }

    private void getDirection(){
        //this method is used to get the direction between the source and destination
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        boundsBuilder.include(src.getLatLng());
        boundsBuilder.include(dst.getLatLng());
        LatLngBounds bounds = boundsBuilder.build();
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        fetchAndDisplayRoute();
    }
    private void fetchAndDisplayRoute() {
        //this method is used to get the route between the source and destination
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getString(R.string.my_api_key)) // Replace with your API key
                .build();

        DirectionsApiRequest req = DirectionsApi.newRequest(context)
                .origin(new com.google.maps.model.LatLng(src.getLatLng().latitude, src.getLatLng().longitude))
                .destination(new com.google.maps.model.LatLng(dst.getLatLng().latitude, dst.getLatLng().longitude))
                .mode(TravelMode.DRIVING);
        ;

        req.setCallback(new PendingResult.Callback<DirectionsResult>() {
            //this method is used to get the result of the route request
            @Override
            public void onResult(DirectionsResult result) {
                runOnUiThread(() -> {
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .color(Color.BLUE)
                            .width(8);

                    for (com.google.maps.model.LatLng point : result.routes[0].overviewPolyline.decodePath()) {
                        polylineOptions.add(new LatLng(point.lat, point.lng));
                    }
                    Polyline polyline = map.addPolyline(polylineOptions);
                    v.add(polyline);
                    addToDatabase(polyline);
                });
            }

            @Override
            public void onFailure(Throwable e) {
                // Handle error
            }
        });

    }
    void addToDatabase(Polyline polyline) {
        //this method is used to add the route to the database
        String userId = auth.getCurrentUser().getEmail();
        List<LatLng> points = polyline.getPoints();
        HashMap<String, Object> path = new HashMap<>();
        User user=new User();
        user.route=points;
        user.uid=auth.getCurrentUser().getUid();
        path.put(userId, user);
        fstore.collection("paths").document(user.uid).set(path)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //this method is used to check if the route is added to the database
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomePage.this, "Your ride is successfully shared", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    //this method is used to check if the route is not added to the database
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomePage.this, "Error adding path to database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void geoLocate() {
        //this method is used to get the location of the source and destination
        Log.d(TAG, "geoLocate: geolocating");
        String searchString = src.getName();
        Geocoder geocoder = new Geocoder(HomePage.this);
        List<Address> list = new ArrayList<>();
        try{
            list=geocoder.getFromLocationName(searchString,1);
        }
        catch(IOException hi){
            Log.e(TAG, "geoLocate: IOException: " + hi.getMessage() );
        }
        if(list.size()>0){
            Address address=list.get(0);
            Log.d(TAG, "geoLocate: found a location: "+address.toString());
            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),15f, address.getAddressLine(0));
        }
    }

    @Override
    public void onBackPressed() {   //this method is used to close the navigation view when back button is pressed
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //this method is used to navigate to different pages from the navigation view
        if (item.getItemId()==R.id.nav_home) {
            //if home is selected in the navigation view then the user is taken to the home page
            Intent intent=new Intent (HomePage.this, OptionsActivity.class);
            startActivity(intent);
            finish();
        }
        else if(item.getItemId()==R.id.nav_profile) {
            //if profile is selected in the navigation view then the user is taken to the profile page
            Intent intent=new Intent (HomePage.this, ProfilePage.class);
            startActivity(intent);
        }
        else if(item.getItemId()==R.id.nav_logout) {
            //if logout is selected in the navigation view then the user is logged out and taken to the login page
            auth.signOut();
            Toast.makeText(this, "Log Out SuccessFull", Toast.LENGTH_SHORT).show();
            Intent intent1=new Intent(HomePage.this, Login.class);
            startActivity(intent1);
            finish();

        }
        else if(item.getItemId()==R.id.nav_trip)
        {
            //if trip is selected in the navigation view then the user is taken to the trip details page
            Intent intent2=new Intent(HomePage.this, Travel_Details.class);
            startActivity(intent2);
        }
        return true;
    }

    private void getDeviceLocation(){
        //this method is used to get the current location of the user
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();
                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 15f,"My Location");

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(HomePage.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }
    private void moveCamera(LatLng latLng, float zoom,String title){
        //this method is used to move the camera to the location specified by the user
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        MarkerOptions options=new MarkerOptions().position(latLng).title(title);
        map.addMarker(options);
    }
    public void onMapReady(GoogleMap googleMap) {
        //this method is used to get the map ready
        Log.d(TAG, "onMapReady: map is ready");
        map = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }
    private void getLocationPermission() {
        //this method is used to get the location permission from the user
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                init();
            }
            else {
                ActivityCompat.requestPermissions(this,permissions,1234);
            }
        }else{
            ActivityCompat.requestPermissions(this, permissions,1234);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is used to get the result of the permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case 1234: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    init();
                }
            }
        }
    }
}
