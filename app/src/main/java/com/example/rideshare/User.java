package com.example.rideshare;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class User {
    //This  class is used to store the user Object in the database.
    public List<LatLng> route;
    // This is the List of Latitude and Longitude of the route of the user traveling.
    public String uid;
    // This is the unique id of the user.
}
