package com.example.aksha.beachhackhunt;

import com.google.android.gms.maps.model.LatLng;

public class Marker {

    private String latitude;
    private String longitude;
    private String clue;
    private String soln;
    private String title;
    private String isFinal;
    private boolean isVisible = false;
    private boolean isMultimedia;

    public boolean isMultimedia() {
        return isMultimedia;
    }

    public void setMultimedia(boolean multimedia) {
        isMultimedia = multimedia;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(String isFinal) {
        this.isFinal = isFinal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Marker{" +
                "latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", clue='" + clue + '\'' +
                ", soln='" + soln + '\'' +
                '}';
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public String getSoln() {
        return soln;
    }

    public void setSoln(String soln) {
        this.soln = soln;
    }
}
