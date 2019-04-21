package com.example.aksha.beachhackhunt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class huntMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    public static GoogleMap mMap;
    LocationManager locationManager ;
    String provider;
    Location mylocation;
    Game game = new Game();
    public static int markerNo;
    public static ArrayList<com.google.android.gms.maps.model.Marker> markersGoogle = new ArrayList<>();
    public static int correctAnswer=0;
    public static boolean finalMarker=false;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://beachhack-5bdd6.firebaseio.com/");
    DatabaseReference databaseReference = firebaseDatabase.getReference("HuntMarker");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_maps);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Creating an empty criteria object
        Criteria criteria = new Criteria();

        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);

        if(provider!=null && !provider.equals("")){

            if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {


            }

            // Get the location from the given provider
            mylocation = locationManager.getLastKnownLocation(provider);

            locationManager.requestLocationUpdates(provider, 20000, 1, this);

            if(mylocation!=null)
                onLocationChanged(mylocation);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                game.setTotalMarkers(Integer.parseInt(dataSnapshot.child("TotalMarkers").getValue().toString()));
                for (int i = 1; i <= game.getTotalMarkers(); i++) {
                    Marker marker = new Marker();
                    marker.setClue(dataSnapshot.child("Marker" + i).child("Clue").getValue().toString());
                    marker.setLatitude(dataSnapshot.child("Marker" + i).child("Lat").getValue().toString());
                    marker.setLongitude(dataSnapshot.child("Marker" + i).child("Lon").getValue().toString());
                    marker.setSoln(dataSnapshot.child("Marker" + i).child("Sol").getValue().toString());
                    marker.setTitle(dataSnapshot.child("Marker" + i).child("Title").getValue().toString());
                    marker.setIsFinal(dataSnapshot.child("Marker" + i).child("isFinal").getValue().toString());
                    game.addMarker(marker);
                }

                ArrayList<Marker> markerArrayList = game.getMarkerList();

                float zoomLevel = 17.0f;
                int k=1;
                com.google.android.gms.maps.model.Marker markerTemp;
                for (int i = 0; i < game.getTotalMarkers(); i++) {
                    Marker marker = markerArrayList.get(i);
                    LatLng latLng = new LatLng(Double.parseDouble(marker.getLatitude()), Double.parseDouble(marker.getLongitude()));
                    markerTemp = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker" + k));
                    markerTemp.hideInfoWindow();
                    markersGoogle.add(markerTemp);
                    k=k+1;
                }

                com.google.android.gms.maps.model.Marker mrkr;
                for (int i = 0; i<markersGoogle.size();i++) {
                    mrkr = markersGoogle.get(i);
                    if(i==0)
                        mrkr.setVisible(true);
                    else
                        mrkr.setVisible(false);
                }


                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                }
                mMap.setMyLocationEnabled(true);
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoomLevel));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        String question = new String();
                        String answer = new String();

                        for (int i = 0; i < game.getTotalMarkers(); i++) {
                            Log.e("Marker clicked Title", marker.getTitle());
                            Log.e("Marker list Title", game.getMarkerList().get(i).getTitle());
                            float[] results = new float[1];
                            Location.distanceBetween(mylocation.getLatitude(), mylocation.getLongitude(), Double.parseDouble(game.getMarkerList().get(i).getLatitude()), Double.parseDouble(game.getMarkerList().get(i).getLongitude()), results);
//                            double results = DistanceBetween(mylocation.getLatitude(), mylocation.getLongitude(),Double.parseDouble(game.getMarkerList().get(i).getLatitude()), Double.parseDouble(game.getMarkerList().get(i).getLongitude()) );

                            if (results[0] < 100.0){
                                if (game.getMarkerList().get(i).getTitle().equals(marker.getTitle())) {
                                    question = game.getMarkerList().get(i).getClue();
                                    answer = game.getMarkerList().get(i).getSoln();
                                    if(game.getMarkerList().get(i).getIsFinal().equals("1")){
                                        finalMarker = true;
                                    }

                                    Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                                    intent.putExtra("question", question);
                                    intent.putExtra("answer", answer);
                                    marker.setVisible(false);
                                    startActivity(intent);
                                    return true;
                                }

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "You are not close enough to access the clue", Toast.LENGTH_SHORT).show();
                                break;
                            }

                        }
                        return false;
                    }

                });

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Yo", "Failed to read value.", error.toException());
            }
        });

    }


    public double DistanceBetween(Double myLocationLatitude, Double myLocationLongitude, Double markerLatitide, Double markerLongitude){
//        double dlon = myLocationLongitude - markerLongitude;
//        double dlat = myLocationLatitude - markerLatitide;
//        double a = Math.pow(Math.sin(dlat/2),2) + Math.cos(markerLatitide) * Math.cos(myLocationLatitude) * Math.pow(Math.sin(dlon/2),2);
//        double c = 2 * Math.atan2( Math.sqrt(a), Math.sqrt(1-a) );
//        double d = 6373 * c * 1000;
//        return  d;


        double d = Math.acos(Math.sin(Math.PI*myLocationLatitude/180.0)*Math.sin(Math.PI*markerLatitide/180.0)+Math.cos(Math.PI*myLocationLatitude/180.0)*Math.cos(Math.PI*markerLatitide/180.0)*Math.cos(Math.PI*myLocationLongitude/180.0-Math.PI*markerLongitude/180.0))*6378;
        return d;
    }

    @Override
    public void onLocationChanged(Location location) {


        // Getting reference to TextView tv_longitude
        mylocation.setLatitude(location.getLatitude());
        mylocation.setLongitude(location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


//        float zoomLevel = 20.0f;
//
//
//        // Add a marker in Sydney and move the camera
//        for (int i = 0; i<game.getTotalMarkers();i++ ){
//            Marker marker = markerArrayList.get(i);
//            LatLng latLng = new LatLng(Double.parseDouble(marker.getLatitude()), Double.parseDouble(marker.getLongitude()));
//            mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"+i));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));



    }
}
