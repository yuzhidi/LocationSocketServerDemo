package com.hicam.locationservice;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation {
    Context mContext;
    private Location mLocation;

    public MyLocation(Context c) {
        mContext = c;
    }

    private void init() {
        LocationManager loctionManager;
        String contextService = Context.LOCATION_SERVICE;
        loctionManager = (LocationManager) mContext
                .getSystemService(contextService);

        // GPS
        // String provider=LocationManager.GPS_PROVIDER;
        // Location location = loctionManager.getLastKnownLocation(provider);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String provider = loctionManager.getBestProvider(criteria, true);

        if (provider != null) {
            Location location = loctionManager.getLastKnownLocation(provider);

            updateLocation(location);

            // Note:the parameter should consider power usage
            // minTime 100ms, minDistance 1 meters
            loctionManager.requestLocationUpdates(provider, 100, 1,
                    locationListener);

        }
    }

    private void updateLocation(Location location) {
        mLocation = new Location(location);
    }

    Location getLocation() {
        return mLocation;
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
    };
}
