package com.example.aksha.beachhackhunt;

import java.util.ArrayList;

public class Game {

    private ArrayList<Marker> markerList = new ArrayList<>();

    private int totalMarkers;

    public void addMarker(Marker marker){
        markerList.add(marker);
    }

    public int getTotalMarkers() {
        return totalMarkers;
    }

    public void setTotalMarkers(int totalMarkers) {
        this.totalMarkers = totalMarkers;
    }

    public ArrayList<Marker> getMarkerList() {
        return markerList;
    }
}
