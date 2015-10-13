package com.twyst.app.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.twyst.app.android.service.LocationService;
import com.twyst.app.android.util.AppConstants;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by Vipul Sharma on 10/13/2015.
 */
public class LocationOfflineList implements Serializable{
    private LinkedList<LocationOffline> fifo;

    public LinkedList<LocationOffline> getFifo() {
        if (fifo==null)
            fifo = new LinkedList<LocationOffline>();
        return fifo;
    }

    public void setFifo(LinkedList<LocationOffline> fifo) {
        this.fifo = fifo;
    }

    private void removeFirst(){
        if (!getFifo().isEmpty())
            getFifo().removeFirst();
    }

    public void addToLast(LocationOffline locationOffline,int maxSize){
        if (getFifo().size()>= maxSize){
            removeFirst();
        }
        getFifo().add(locationOffline);
    }

}
