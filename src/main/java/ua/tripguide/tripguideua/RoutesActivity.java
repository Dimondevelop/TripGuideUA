package ua.tripguide.tripguideua;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Utils.GetDirectionsData;

import static java.util.Objects.requireNonNull;

public class RoutesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final String GOOGLE_API_KEY = "AIzaSyA-1OzdivurrFOMUPOYBV6QaKwMArDoR5I";
    StringBuilder url;

    LatLng[] latLngs;

//    LatLng latLngStart = new LatLng(48.258164, 25.929889);
//    LatLng latLng1 = new LatLng(48.268377, 25.929941);
//    LatLng latLng2 = new LatLng(48.296992, 25.922283);
//    LatLng latLng3 = new LatLng(48.286992, 25.942283);
//    LatLng latLngEnd = new LatLng(48.279832, 25.937631);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        Intent intent = getIntent();
        float[] coordinates_y = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_y");
        float[] coordinates_x = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_x");

        if (coordinates_x != null && coordinates_y != null) {
            int countCoordinates = coordinates_x.length;
            latLngs = new LatLng[countCoordinates];
            for (int i = 0; i < countCoordinates; i++) {
                latLngs[i] = new LatLng(coordinates_x[i],coordinates_y[i]);
            }
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "mapFragment is null!", Toast.LENGTH_SHORT);
            toast.show();

        }
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
        if (latLngs != null){
            int countLatLngs = latLngs.length;
            url = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/directions/json?")
                    .append("origin=").append(latLngs[0].latitude).append(",").append(latLngs[0].longitude)
                    .append("&destination=").append(latLngs[countLatLngs-1].latitude).append(",").append(latLngs[countLatLngs-1].longitude);

            if (countLatLngs > 2) {
                url.append("&waypoints=").append(latLngs[1].latitude).append(",").append(latLngs[1].longitude);

                if (countLatLngs > 3)
                    for (int i = 2; i < countLatLngs-1; i++){
                        url.append("|").append(latLngs[i].latitude).append(",").append(latLngs[i].longitude);
                    }
            }

            url.append("&mode=walking")
                    .append("&key=" + GOOGLE_API_KEY)
                    .append("&alternatives=false");

            Object[] dataTransfer = new Object[4];

            GetDirectionsData getDirectionsData = new GetDirectionsData(getApplicationContext());
            dataTransfer[0] = mMap;
            dataTransfer[1] = url.toString();
            dataTransfer[2] = new LatLng(latLngs[0].latitude, latLngs[0].longitude);
            dataTransfer[3] = new LatLng(latLngs[countLatLngs-1].latitude, latLngs[countLatLngs-1].longitude);

            getDirectionsData.execute(dataTransfer);

            for (int i =0; i<latLngs.length;i++){
                mMap.addMarker(new MarkerOptions().position(latLngs[i]).snippet("Snippet")
                        .title("title "+i));
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs[0]));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 16), 50, null);
        }




    }
}
