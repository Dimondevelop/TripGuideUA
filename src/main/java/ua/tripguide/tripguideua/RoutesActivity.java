package ua.tripguide.tripguideua;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;
import java.util.Objects;

import ua.tripguide.tripguideua.Utils.GetDirectionsData;
import ua.tripguide.tripguideua.Utils.PermissionUtils;
import ua.tripguide.tripguideua.Utils.RequestBuilder;


public class RoutesActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private static final String TAG = RoutesActivity.class.getSimpleName();
    private GoogleMap mMap;
    String placeName;
    private CameraPosition mCameraPosition;

    RequestBuilder requestBuilder;

    Context mContext = this;

    private Location mLastKnownLocation;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;


    private LatLng[] latLngs;
    int countLatLngs;
    String[] titles;
    String[] working_hours;
    String[] place_ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        Locale.setDefault(new Locale("uk_UA"));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(mContext);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(mContext);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        Intent intent = getIntent();
        float[] coordinates_y = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_y");
        float[] coordinates_x = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_x");
        titles = Objects.requireNonNull(intent.getExtras()).getStringArray("titles");
        working_hours = Objects.requireNonNull(intent.getExtras()).getStringArray("working_hours");
        place_ids = Objects.requireNonNull(intent.getExtras()).getStringArray("place_ids");


        if (coordinates_x != null && coordinates_y != null) {
            countLatLngs = coordinates_x.length;
            latLngs = new LatLng[countLatLngs];
            for (int i = 0; i < countLatLngs; i++) {
                latLngs[i] = new LatLng(coordinates_x[i], coordinates_y[i]);
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

        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);
        enableMyLocation();

        requestBuilder = new RequestBuilder();
        String url = requestBuilder.buildUrl(latLngs);

        Object[] dataTransfer = new Object[4];

        GetDirectionsData getDirectionsData = new GetDirectionsData(mContext);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(latLngs[0].latitude, latLngs[0].longitude);
        dataTransfer[3] = new LatLng(latLngs[countLatLngs - 1].latitude, latLngs[countLatLngs - 1].longitude);

        getDirectionsData.execute(dataTransfer);
        final int countPlaceIds = place_ids.length;

        mGeoDataClient.getPlaceById(place_ids).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                if (task.isSuccessful()) {
                    PlaceBufferResponse places = task.getResult();
                    Place[] myPlace = new Place[countPlaceIds];
                    for (int i = 0; i < countPlaceIds; i++) {
                        myPlace[i] = places.get(i);

                        Log.i(TAG, "Place found(debug): " + myPlace[i].getName() + " " + myPlace[i].getLatLng().latitude + "," + myPlace[i].getLatLng().longitude +
                                " | \n" + myPlace[i].getAddress() + " |  " + myPlace[i].getAttributions() + " |  " + myPlace[i].getLocale() + " |  "
                                + myPlace[i].getPlaceTypes() + " |  " + myPlace[i].getPriceLevel() + " |  " + myPlace[i].getPhoneNumber());
                    }

                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.circle_small_white);
                    Bitmap waypointMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 30, 30, false);
                    for (int i = 0; i < latLngs.length; i++) {
                        mMap.addMarker(new MarkerOptions().position(latLngs[i]).snippet(working_hours[i])
                                .title(myPlace[i].getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(waypointMarker)).anchor(0.5f, 0.5f));
                    }

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngs[0]));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], DEFAULT_ZOOM), 50, null);

                    places.release();
                } else {
                    Log.e(TAG, "Place not found.");
                }
            }
        });


    }

    /**
     * Вмикає місцезнаходження, якщо дозвіл на точне місцезнаходження було надано.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        getDeviceLocation();

//        Toast.makeText(this, "MyLocation button clicked ", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
        return true;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            getLocationPermission();
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            assert mLastKnownLocation != null;
                            LatLng[] latLngsNew = new LatLng[latLngs.length + 1];
                            latLngsNew[0] = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            int i = 1;
                            int j = 0;
                            for (; i < latLngs.length + 1; i++) {
                                latLngsNew[i] = latLngs[j++];
                            }
                            String urlNew = requestBuilder.buildUrl(latLngsNew);

                            GetDirectionsData getDirectionsData = new GetDirectionsData(mContext);

                            Object[] dataTransfer = new Object[4];

                            dataTransfer[0] = mMap;
                            dataTransfer[1] = urlNew;
                            dataTransfer[2] = new LatLng(latLngs[0].latitude, latLngs[0].longitude);
                            dataTransfer[3] = new LatLng(latLngs[countLatLngs - 1].latitude, latLngs[countLatLngs - 1].longitude);

                            getDirectionsData.execute(dataTransfer);

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    //Стрілка назад
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
