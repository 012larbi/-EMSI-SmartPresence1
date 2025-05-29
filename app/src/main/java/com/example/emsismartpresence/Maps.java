package com.example.emsismartpresence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Maps extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Marker userMarker;
    private boolean firstLocationUpdate = true;

    // Liste des centres EMSI avec coordonnées et liens Google Maps
    private final Object[][] emsiCenters = {
            // Format: lat, lng, titre, adresse, lien Google Maps
            {33.5892775, -7.6155475, "EMSI Centre 1", "107 Rue Al Bakri, residence niama 3, n. 57 RDC, Casablanca 20250", "https://maps.app.goo.gl/XXXXXX"},
            {33.5918149, -7.6070537, "EMSI Centre 2", "105 Rue Al Bakri, Casablanca 20250", "https://maps.app.goo.gl/n6aZTkM6T4jFBbck7"},
            {33.5926405, -7.6275635, "EMSI Moulay Youssef", "7 Abou Youssef El Kindy, Casablanca 20250", "https://maps.app.goo.gl/ZZZZZZ"},
            {33.5814531, -7.6437751, "EMSI Roudani", "380 Bd Brahim Roudani, Casablanca", "https://maps.app.goo.gl/AAAAAA"},
            {33.5824797, -7.648229, "EMSI Maarif", "Boulvrd Bir Anzarane H9M5+92X, Casablanca 20250", "https://maps.app.goo.gl/BBBBBB"},
            {33.5415145, -7.6838262, "EMSI Les Orangers", "Bd Laymoune, Casablanca", "https://maps.app.goo.gl/CCCCCC"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "Erreur: Fragment de carte non trouvé", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Ajout des marqueurs pour les centres EMSI
        for (Object[] center : emsiCenters) {
            LatLng location = new LatLng((double)center[0], (double)center[1]);
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title((String)center[2])
                    .snippet("Adresse: " + center[3] + "\nCliquez pour ouvrir dans Google Maps")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        }

        // Gestion du clic sur l'infobulle
        mMap.setOnInfoWindowClickListener(marker -> {
            for (Object[] center : emsiCenters) {
                if (marker.getTitle().equals(center[2])) {
                    String mapUrl = (String)center[4];
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUrl));
                    startActivity(intent);
                    break;
                }
            }
        });

        // Configuration de la carte
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        // Centrage initial sur Casablanca
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(33.5731, -7.5898), 12));

        // Gestion de la localisation
        if (checkLocationPermission()) {
            enableMyLocation();
        }
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void enableMyLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                startLocationUpdates();
            }
        } catch (SecurityException e) {
            Toast.makeText(this, "Erreur de permission: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    updateUserLocation(location);
                }
            }
        };

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (SecurityException e) {
            Toast.makeText(this, "Erreur de sécurité: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateUserLocation(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker == null) {
            userMarker = mMap.addMarker(new MarkerOptions()
                    .position(userLocation)
                    .title("Votre position")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        } else {
            userMarker.setPosition(userLocation);
        }

        if (firstLocationUpdate) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            firstLocationUpdate = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this,
                        "La permission de localisation est nécessaire pour certaines fonctionnalités",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkLocationPermission()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}