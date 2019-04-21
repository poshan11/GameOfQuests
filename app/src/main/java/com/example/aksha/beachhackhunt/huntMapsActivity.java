package com.example.aksha.beachhackhunt;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
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

    FirebaseDatabase firebaseDatabase ;
    DatabaseReference databaseReference ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hunt_maps);
        firebaseDatabase =  FirebaseDatabase.getInstance("https://gameoftreasures-1555808035264.firebaseio.com/");
        databaseReference = firebaseDatabase.getReference(getIntent().getStringExtra("gameName").toString());

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
                game.setTotalMarkers(Integer.parseInt(dataSnapshot.child("totalMarker").getValue().toString()));
                for (int i = 1; i <= game.getTotalMarkers(); i++) {
                    Marker marker = new Marker();
                    marker.setClue(dataSnapshot.child("Marker" + i).child("Clue").getValue().toString());
                    marker.setLatitude(dataSnapshot.child("Marker" + i).child("Lat").getValue().toString());
                    marker.setLongitude(dataSnapshot.child("Marker" + i).child("Lng").getValue().toString());
                    marker.setSoln(dataSnapshot.child("Marker" + i).child("Sol").getValue().toString());
                    marker.setTitle(dataSnapshot.child("Marker" + i).child("Title").getValue().toString());
                    marker.setIsFinal(dataSnapshot.child("Marker" + i).child("isFinal").getValue().toString());
                    marker.setMultimedia(Boolean.valueOf(dataSnapshot.child("Marker" + i).child("isMultiMedia").getValue().toString()));
                    if (i==1){
                        marker.setVisible(true);
                    }
                    game.addMarker(marker);
                }

                ArrayList<Marker> markerArrayList = game.getMarkerList();

                float zoomLevel = 17.0f;
                int k=1;
                com.google.android.gms.maps.model.Marker markerTemp;
                for (int i = 0; i < game.getTotalMarkers(); i++) {
                    Marker marker = markerArrayList.get(i);
                    LatLng latLng = new LatLng(Double.parseDouble(marker.getLatitude()), Double.parseDouble(marker.getLongitude()));
                    markerTemp = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker" + k).icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_diamond)));
                    markerTemp.hideInfoWindow();
                    markersGoogle.add(markerTemp);
                    k=k+1;
                }

                com.google.android.gms.maps.model.Marker mrkr;
                for (int i = 0; i<markersGoogle.size();i++) {
                    mrkr = markersGoogle.get(i);
                    if (game.getMarkerList().get(i).isVisible()){
                        mrkr.setVisible(true);
                    }
                    else{
                        mrkr.setVisible(false);
                    }

                }


                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                }


                mMap.setMyLocationEnabled(true);
                LatLng myLatLng = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLatLng).tilt(70).zoom(19).bearing(0).build();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, zoomLevel));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                        String question = new String();
                        String answer = new String();

                        for (int i = 0; i < game.getTotalMarkers(); i++) {
                            float[] results = new float[1];
                            Location.distanceBetween(mylocation.getLatitude(), mylocation.getLongitude(), Double.parseDouble(game.getMarkerList().get(i).getLatitude()), Double.parseDouble(game.getMarkerList().get(i).getLongitude()), results);
                            if (results[0] < 50000.0){
                                if (game.getMarkerList().get(i).getTitle().equals(marker.getTitle())) {
                                    question = game.getMarkerList().get(i).getClue();
                                    answer = game.getMarkerList().get(i).getSoln();
                                    if(game.getMarkerList().get(i).getIsFinal().equals("1")){
                                        finalMarker = true;
                                    }

                                    Intent intent = new Intent(getApplicationContext(), QuestionActivity.class);
                                    intent.putExtra("question", question);
                                    intent.putExtra("answer", answer);
                                    intent.putExtra("index", i);
                                    intent.putExtra("isMultimedia", game.getMarkerList().get(i).isMultimedia());
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        markersGoogle.clear();
        finish();
    }

    public double DistanceBetween(Double myLocationLatitude, Double myLocationLongitude, Double markerLatitide, Double markerLongitude){
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



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this,R.raw.style_json));

            if (!success) {
                Log.e("hi", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("hi", "Can't find style. Error: ", e);
        }




    }


    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
