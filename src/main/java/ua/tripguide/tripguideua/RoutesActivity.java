package ua.tripguide.tripguideua;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ua.tripguide.tripguideua.Utils.GetDirectionsData;

public class RoutesActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    static final String GOOGLE_API_KEY = "AIzaSyA-1OzdivurrFOMUPOYBV6QaKwMArDoR5I";
    StringBuilder url;
    LatLng latLngStart = new LatLng(48.258164, 25.929889);
    LatLng latLng1 = new LatLng(48.268377, 25.929941);
    LatLng latLng2 = new LatLng(48.296992, 25.922283);
    LatLng latLng3 = new LatLng(48.286992, 25.942283);
    LatLng latLngEnd = new LatLng(48.279832, 25.937631);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);


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



        url = new StringBuilder()
                .append("https://maps.googleapis.com/maps/api/directions/json?")
                .append("origin=").append(latLngStart.latitude).append(",").append(latLngStart.longitude)
                .append("&destination=").append(latLngEnd.latitude).append(",").append(latLngEnd.longitude)
                .append("&waypoints=").append(latLng1.latitude).append(",").append(latLng1.longitude)
                .append("|").append(latLng2.latitude).append(",").append(latLng2.longitude)
                .append("|").append(latLng3.latitude).append(",").append(latLng3.longitude)
                .append("&mode=walking")
                .append("&key=" + GOOGLE_API_KEY)
                .append("&alternatives=false");

        Object[] dataTransfer = new Object[4];

        GetDirectionsData getDirectionsData = new GetDirectionsData(getApplicationContext());
        dataTransfer[0] = mMap;
        dataTransfer[1] = url.toString();
        dataTransfer[2] = new LatLng(latLngStart.latitude,latLngStart.longitude);
        dataTransfer[3] = new LatLng(latLngEnd.latitude,latLngEnd.longitude);

        getDirectionsData.execute(dataTransfer);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngStart));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngStart, 16), 50, null);

    }
}
