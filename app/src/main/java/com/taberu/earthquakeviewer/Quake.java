package com.taberu.earthquakeviewer;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Taberu on 01/11/2016.
 */

public class Quake {

    private Date date;
    private String details;
    private Location location;
    private double magnitude;
    private String link;

    public Date getDate() { return date; }
    public String getDetails() { return details; }
    public Location getLocation() { return location; }
    public double getMagnitude() { return magnitude; }
    public String getLink() { return link; }

    public Quake(Date _d, String _details, Location _loc, double _mag) {
        date = _d;
        details = _details;
        location = _loc;
        magnitude = _mag;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String quakeString = sdf.format(date) + ":" + magnitude + "" + details;

        return quakeString;
    }
}
