package ua.tripguide.tripguideua;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.Utils.DBHelper;
import ua.tripguide.tripguideua.Utils.GetDirectionsData;
import ua.tripguide.tripguideua.Utils.PermissionUtils;
import ua.tripguide.tripguideua.Utils.RequestBuilder;
import ua.tripguide.tripguideua.Adapters.PopupAdapter;
import ua.tripguide.tripguideua.Utils.UtilMethods;

public class RoutesActivity extends AppCompatActivity implements
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnInfoWindowClickListener,
        OnMapReadyCallback {

    private static final String TAG = RoutesActivity.class.getSimpleName();
    private GoogleMap mMap;
    LocationManager manager;

    RequestBuilder requestBuilder;

    Context mContext = this;

    private Location mLastKnownLocation;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 19;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    private LatLng[] latLngs;
    int countLatLngs, cityId;
    boolean breakTime;
    String[] place_ids;
    String[] titles;
    String[] working_hours;
    int[] average_duration;
    int[] price;

    ArrayList<RouteObjectsInfo> lstRouteObjectsInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        Locale.setDefault(new Locale("uk_UA"));

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        Intent intent = getIntent();
        breakTime = Objects.requireNonNull(intent.getExtras()).getBoolean("breakTime");
        cityId = Objects.requireNonNull(intent.getExtras()).getInt("cityId");
        float[] coordinates_y = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_y");
        float[] coordinates_x = Objects.requireNonNull(intent.getExtras()).getFloatArray("coordinates_x");
        place_ids = Objects.requireNonNull(intent.getExtras()).getStringArray("place_ids");
        titles = Objects.requireNonNull(intent.getExtras()).getStringArray("titles");
        working_hours = Objects.requireNonNull(intent.getExtras()).getStringArray("working_hours");
        average_duration = Objects.requireNonNull(intent.getExtras()).getIntArray("average_duration");
        price = Objects.requireNonNull(intent.getExtras()).getIntArray("price");

        if (coordinates_x != null && coordinates_y != null) {
            countLatLngs = coordinates_x.length;
            latLngs = new LatLng[countLatLngs];
            for (int i = 0; i < countLatLngs; i++) {
                latLngs[i] = new LatLng(coordinates_x[i], coordinates_y[i]);
            }
        }

        for (int i = 0; i < countLatLngs; i++) {
            lstRouteObjectsInfos.add(new RouteObjectsInfo(place_ids[i], titles[i], Objects.requireNonNull(working_hours)[i],
                    Objects.requireNonNull(average_duration)[i], Objects.requireNonNull(price)[i], latLngs[i]));
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

    public static ArrayList<RouteObjectsInfo> sortLatLng(ArrayList<RouteObjectsInfo> routeObjectsInfoList) {
        int count = routeObjectsInfoList.size();
        int index = 1;
        int lastIndex = count - 1;
        double[] distances = new double[lastIndex];

        for (int i = 0; i < count - 1; i++) {
            distances[i] = SphericalUtil.computeDistanceBetween(routeObjectsInfoList.get(0).getLatLng(), routeObjectsInfoList.get(i + 1).getLatLng());
        }

        double temp = distances[0];
        for (int i = 1; i < lastIndex; i++) {
            if (temp < distances[i]) {
                temp = distances[i];
                index = i + 1;
            }
        }
        RouteObjectsInfo tempLastROI = new RouteObjectsInfo(routeObjectsInfoList.get(lastIndex));
        routeObjectsInfoList.set(lastIndex, new RouteObjectsInfo(routeObjectsInfoList.get(index)));
        routeObjectsInfoList.set(index, new RouteObjectsInfo(tempLastROI));

        return routeObjectsInfoList;
    }

    private ArrayList<RouteObjectsInfo> doubleSort(ArrayList<RouteObjectsInfo> routeObjectsInfoList) {

        int count = routeObjectsInfoList.size();
        int lastIndex = count - 1;

        routeObjectsInfoList = new ArrayList<>(RoutesActivity.sortLatLng(routeObjectsInfoList));

        RouteObjectsInfo temp_before = new RouteObjectsInfo(routeObjectsInfoList.get(0));
        routeObjectsInfoList.set(0, new RouteObjectsInfo(routeObjectsInfoList.get(lastIndex)));
        routeObjectsInfoList.set(lastIndex, new RouteObjectsInfo(temp_before));

        routeObjectsInfoList = new ArrayList<>(sortLatLng(routeObjectsInfoList));
        RouteObjectsInfo temp_after = new RouteObjectsInfo(routeObjectsInfoList.get(0));

        routeObjectsInfoList.get(0).setPlace_id(routeObjectsInfoList.get(lastIndex).getPlace_id());
        routeObjectsInfoList.set(0, new RouteObjectsInfo(routeObjectsInfoList.get(lastIndex)));

        routeObjectsInfoList.set(lastIndex, new RouteObjectsInfo(temp_after));

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

        if (breakTime){
            DBHelper dbHelper = new DBHelper(mContext);
            ArrayList<ObjectList> lstObjectBreakList = dbHelper.getObjectsFromDB(cityId, true);

            lstRouteObjectsInfos = doubleSort(lstRouteObjectsInfos);
            int countAvg = 0;

            for (int i = 0; i < lstRouteObjectsInfos.size(); i++) {
                countAvg += lstRouteObjectsInfos.get(i).getAverage_duration();
                if (countAvg >= 60 * 2) {
                    lstRouteObjectsInfos.add(i, UtilMethods.nearestObject(lstRouteObjectsInfos.get(i), lstObjectBreakList));
                    break;
                }
            }
        } else {
            lstRouteObjectsInfos = doubleSort(lstRouteObjectsInfos);
        }

        requestBuilder = new RequestBuilder();
        String url = requestBuilder.buildUrl(lstRouteObjectsInfos);

        Object[] dataTransfer = new Object[2];

        GetDirectionsData getDirectionsData = new GetDirectionsData(mContext, lstRouteObjectsInfos, DEFAULT_ZOOM);
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        mMap.moveCamera(CameraUpdateFactory.newLatLng(lstRouteObjectsInfos.get(0).getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lstRouteObjectsInfos.get(0).getLatLng(), DEFAULT_ZOOM), 50, null);

        getDirectionsData.execute(dataTransfer);


//||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||DEBUG|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
//        mGeoDataClient.getPlaceById("ChIJi29iwrgINEcRWJ6tTNQGrWI").addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//            @Override
//            public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                if (task.isSuccessful()) {
//                    PlaceBufferResponse places = task.getResult();
//                    Place myPlace;
//                    myPlace = places.get(0);
//                    // DEBUG
//                    Log.i(TAG, "Place found(debug): " + myPlace.getName() + " " + myPlace.getLatLng().latitude + "," + myPlace.getLatLng().longitude +
//                            " | \n" + myPlace.getAddress() + " |  " + myPlace.getAttributions() + " |  " + myPlace.getLocale() + " |  "
//                            + myPlace.getPlaceTypes() + " |  " + myPlace.getPriceLevel() + " |  " + myPlace.getPhoneNumber());
//                    assert places != null;
//                    places.release();
//                } else {
//                    Log.e(TAG, "Place not found.");
//                }
//            }
//        });
//|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||

        mMap.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater()));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return marker.getSnippet().equals("distance($%code#1)");
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

    public boolean isGeoDisabled() {
        LocationManager mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        boolean mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean mIsGeoDisabled = !mIsGPSEnabled && !mIsNetworkEnabled;
        return mIsGeoDisabled;
    }

    private boolean buildAlertMessageNoLocationService(boolean network_enabled) {
        String msg = !network_enabled ? ("Для визначення поточного місцезнаходження потрібно увімкнути геолокацію") : null;

        if (msg != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true)
                    .setMessage(msg).setNegativeButton(" скасувати ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).setPositiveButton(" увімкнути ", new DialogInterface.OnClickListener() {
                public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            final AlertDialog alert = builder.create();
            alert.show();
            return true;
        }
        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getDeviceLocation();
        } else {
            buildAlertMessageNoLocationService(!isGeoDisabled());
        }


//        Toast.makeText(this, "MyLocation button clicked ", Toast.LENGTH_SHORT).show();
//        // Return false so that we don't consume the event and the default behavior still occurs
//        // (the camera animates to the user's current position).
        return true;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Мої кординати: " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
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


                            ArrayList<RouteObjectsInfo> lstRouteObjectsInfosWithLocation = new ArrayList<>(lstRouteObjectsInfos);

                            lstRouteObjectsInfosWithLocation.add(new RouteObjectsInfo(null, "Моє місцезнаходження", "", 0,0,
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())));

                            int count = lstRouteObjectsInfosWithLocation.size();

                            RouteObjectsInfo temp_after = new RouteObjectsInfo(lstRouteObjectsInfosWithLocation.get(0));
                            lstRouteObjectsInfosWithLocation.set(0, new RouteObjectsInfo(lstRouteObjectsInfosWithLocation.get(count - 1)));
                            lstRouteObjectsInfosWithLocation.set(count - 1, new RouteObjectsInfo(temp_after));

                            mMap.clear();

                            String urlNew = requestBuilder.buildUrl(sortLatLng(lstRouteObjectsInfosWithLocation));

                            GetDirectionsData getDirectionsData = new GetDirectionsData(mContext, lstRouteObjectsInfosWithLocation, DEFAULT_ZOOM);

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

    private void setAppTitle(String s){
        this.setTitle(s);
    }

}
