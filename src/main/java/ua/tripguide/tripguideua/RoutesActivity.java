package ua.tripguide.tripguideua;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.Utils.GetDirectionsData;
import ua.tripguide.tripguideua.Utils.PermissionUtils;
import ua.tripguide.tripguideua.Utils.RequestBuilder;
import ua.tripguide.tripguideua.Utils.PopupAdapter;

public class RoutesActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback, GoogleMap.OnInfoWindowClickListener {


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
    String[] place_ids;
    String[] titles;
    String[] working_hours;

    ArrayList<RouteObjectsInfo> lstRouteObjectsInfos = new ArrayList<>();


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
        place_ids = Objects.requireNonNull(intent.getExtras()).getStringArray("place_ids");
        titles = Objects.requireNonNull(intent.getExtras()).getStringArray("titles");
        working_hours = Objects.requireNonNull(intent.getExtras()).getStringArray("working_hours");


        if (coordinates_x != null && coordinates_y != null) {
            countLatLngs = coordinates_x.length;
            latLngs = new LatLng[countLatLngs];
            for (int i = 0; i < countLatLngs; i++) {
                latLngs[i] = new LatLng(coordinates_x[i], coordinates_y[i]);
            }
        }

        for (int i = 0; i < countLatLngs; i++) {
            lstRouteObjectsInfos.add(new RouteObjectsInfo(place_ids[i], titles[i], Objects.requireNonNull(working_hours)[i], latLngs[i]));
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

    private ArrayList<RouteObjectsInfo> sortLatLng(ArrayList<RouteObjectsInfo> routeObjectsInfoList) {
        int count = latLngs.length;
        int index = 1;
        double[] distances = new double[count - 1];

//        AB = √(xb - xa)**2 + (yb - ya)**2 - формула дистанції
        for (int i = 0; i < count - 1; i++) {
            distances[i] = Math.sqrt(Math.pow(routeObjectsInfoList.get(0).getLatLng().latitude - routeObjectsInfoList.get(i + 1).getLatLng().latitude, 2)
                    + Math.pow(routeObjectsInfoList.get(0).getLatLng().longitude - routeObjectsInfoList.get(i + 1).getLatLng().longitude, 2));
        }

        double temp = distances[0];
        for (int i = 1; i < count - 1; i++) {
            if (temp < distances[i]) {
                temp = distances[i];
                index = i + 1;
            }
        }

        RouteObjectsInfo tempROI = new RouteObjectsInfo(routeObjectsInfoList.get(count - 1).getPlace_id(), routeObjectsInfoList.get(count - 1).getTitle(),
                routeObjectsInfoList.get(count - 1).getWorking_hour(), routeObjectsInfoList.get(count - 1).getLatLng());

        routeObjectsInfoList.set(count - 1, new RouteObjectsInfo(routeObjectsInfoList.get(index).getPlace_id(), routeObjectsInfoList.get(index).getTitle(),
                routeObjectsInfoList.get(index).getWorking_hour(), routeObjectsInfoList.get(index).getLatLng()));

        routeObjectsInfoList.set(index, new RouteObjectsInfo(tempROI.getPlace_id(), tempROI.getTitle(), tempROI.getWorking_hour(), tempROI.getLatLng()));

        return routeObjectsInfoList;
    }

    private ArrayList<RouteObjectsInfo> doubleSort(ArrayList<RouteObjectsInfo> routeObjectsInfoList) {

        int count = routeObjectsInfoList.size();

        routeObjectsInfoList = new ArrayList<>(sortLatLng(routeObjectsInfoList));

        RouteObjectsInfo temp_before = new RouteObjectsInfo(routeObjectsInfoList.get(0).getPlace_id(), routeObjectsInfoList.get(0).getTitle(),
                routeObjectsInfoList.get(0).getWorking_hour(), routeObjectsInfoList.get(0).getLatLng());

        routeObjectsInfoList.set(0, new RouteObjectsInfo(routeObjectsInfoList.get(count - 1).getPlace_id(), routeObjectsInfoList.get(count - 1).getTitle(),
                routeObjectsInfoList.get(count - 1).getWorking_hour(), routeObjectsInfoList.get(count - 1).getLatLng()));

        routeObjectsInfoList.set(count - 1, new RouteObjectsInfo(temp_before.getPlace_id(), temp_before.getTitle(), temp_before.getWorking_hour(), temp_before.getLatLng()));

        routeObjectsInfoList = new ArrayList<>(sortLatLng(routeObjectsInfoList));

        RouteObjectsInfo temp_after =  new RouteObjectsInfo(routeObjectsInfoList.get(0).getPlace_id(), routeObjectsInfoList.get(0).getTitle(),
                routeObjectsInfoList.get(0).getWorking_hour(), routeObjectsInfoList.get(0).getLatLng());

        routeObjectsInfoList.get(0).setPlace_id(routeObjectsInfoList.get(count - 1).getPlace_id());
        routeObjectsInfoList.set(0, new RouteObjectsInfo(routeObjectsInfoList.get(count - 1).getPlace_id(),routeObjectsInfoList.get(count - 1).getTitle(),
                routeObjectsInfoList.get(count - 1).getWorking_hour(),routeObjectsInfoList.get(count - 1).getLatLng()));

        routeObjectsInfoList.set(count - 1, new RouteObjectsInfo(temp_after.getPlace_id(), temp_after.getTitle(), temp_after.getWorking_hour(), temp_after.getLatLng()));

        return routeObjectsInfoList;
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
        mMap.setOnInfoWindowClickListener(this);
        enableMyLocation();

        requestBuilder = new RequestBuilder();
        lstRouteObjectsInfos = new ArrayList<>(doubleSort(lstRouteObjectsInfos));
        String url = requestBuilder.buildUrl(lstRouteObjectsInfos);

        Object[] dataTransfer = new Object[2];

        GetDirectionsData getDirectionsData = new GetDirectionsData(mContext, lstRouteObjectsInfos);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        getDirectionsData.execute(dataTransfer);

        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lstRouteObjectsInfos.get(0).getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lstRouteObjectsInfos.get(0).getLatLng(), DEFAULT_ZOOM), 50, null);

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

                            lstRouteObjectsInfos.add(0, new RouteObjectsInfo("ChIJ7T8OhbOz0EARtk962u0zNPM", "Моє місцезнаходження", "",
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));

                            lstRouteObjectsInfos = new ArrayList<>(sortLatLng(lstRouteObjectsInfos));
                            String urlNew = requestBuilder.buildUrl(lstRouteObjectsInfos);

                            mMap.clear();
                            GetDirectionsData getDirectionsData = new GetDirectionsData(mContext, lstRouteObjectsInfos);

                            Object[] dataTransfer = new Object[4];

                            dataTransfer[0] = mMap;
                            dataTransfer[1] = urlNew;

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

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }
}
