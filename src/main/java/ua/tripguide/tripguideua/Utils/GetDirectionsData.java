package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ua.tripguide.tripguideua.Models.RouteObjectsInfo;
import ua.tripguide.tripguideua.R;

import static android.support.constraint.Constraints.TAG;

public class GetDirectionsData extends AsyncTask<Object, String, String> implements GoogleMap.OnCameraMoveListener {

    private GoogleMap mMap;
    private ArrayList<RouteObjectsInfo> lstRouteObjectsInfos;
    private int arrayListLegsDistanceCount;
    private MarkerOptions markerOptions;
    private LatLng startLatLng, endLatLng;


    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private int lstSize;
    private int countPlaceIds;
    private String data = "";


    private Context mContext;

    private ArrayList<String> place_ids = new ArrayList<>();
    private ArrayList<String> working_hours = new ArrayList<>();
    private ArrayList<Integer> average_duration = new ArrayList<>();
    private ArrayList<Integer> prices = new ArrayList<>();

    private ArrayList<Object[]> arrayListLegsDistance;

    private List<List<List<LatLng>>> lstLatLngsUpper = new ArrayList<>();
    private List<List<LatLng>> lstLatLngsInner;

    private Marker[] mDistances;

    private int height = 15;
    private int width = 15;
    private int DEFAULT_ZOOM;

    private int countAvgRouteTime = 0;
    private int countRouteDistance = 0;

    public GetDirectionsData(Context mContext, ArrayList<RouteObjectsInfo> lstRouteObjectsInfos, int DEFAULT_ZOOM) {
        this.mContext = mContext;
        this.lstRouteObjectsInfos = lstRouteObjectsInfos;
        this.lstSize = lstRouteObjectsInfos.size();
        this.DEFAULT_ZOOM = DEFAULT_ZOOM;

        for (int i = 0; i < lstSize; i++) {
            if (lstRouteObjectsInfos.get(i).getPlace_id() != null) {
                place_ids.add(lstRouteObjectsInfos.get(i).getPlace_id());
                working_hours.add(lstRouteObjectsInfos.get(i).getWorking_hour());
                average_duration.add(lstRouteObjectsInfos.get(i).getAverage_duration());
                prices.add(lstRouteObjectsInfos.get(i).getPrice());
            }
        }
        countPlaceIds = place_ids.size();


    }

    @Override
    protected String doInBackground(Object... params) {

        mMap = (GoogleMap) params[0];
        String url = (String) params[1];
        startLatLng = lstRouteObjectsInfos.get(0).getLatLng();
        endLatLng = lstRouteObjectsInfos.get(lstRouteObjectsInfos.size() - 1).getLatLng();

        try {
            URL myurl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) myurl.openConnection();
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        mMap.setOnCameraMoveListener(this);

        GeoDataClient mGeoDataClient = Places.getGeoDataClient(mContext);

        try {
            JSONObject jsonObjectResponse = new JSONObject(s);

            JSONArray jsonArrayRoutes = jsonObjectResponse.getJSONArray("routes");
            JSONArray jsonArrayLegs = jsonArrayRoutes.getJSONObject(0).getJSONArray("legs");
            int countJsonArrayLegs = jsonArrayLegs.length();

            ArrayList<ArrayList<String[]>> arrayListLegs = new ArrayList<>();
            arrayListLegsDistance = new ArrayList<>();
            ArrayList<String[]> arrayListSteps = new ArrayList<>();

            LatLng latLngsWaypoints;
            String distanse;
            String duration;
            JSONArray[] jsonArraySteps = new JSONArray[countJsonArrayLegs];

            JSONObject jsonObjectInSteps;
            for (int i = 0; i < countJsonArrayLegs; i++) {
                latLngsWaypoints = new LatLng(Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("end_location").getString("lat")),
                        Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("end_location").getString("lng")));

                distanse = jsonArrayLegs.getJSONObject(i).getJSONObject("distance").getString("value");
                duration = jsonArrayLegs.getJSONObject(i).getJSONObject("duration").getString("value");

                jsonArraySteps[i] = jsonArrayLegs.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < jsonArraySteps[i].length(); j++) {
                    jsonObjectInSteps = jsonArraySteps[i].getJSONObject(j);

                    String polygone = jsonObjectInSteps.getJSONObject("polyline").getString("points");

                    String html_instructions = jsonObjectInSteps.getString("html_instructions");

                    arrayListSteps.add(new String[]{polygone, html_instructions});
                }
                arrayListLegsDistance.add(new Object[]{distanse, duration, latLngsWaypoints});
                arrayListLegs.add(new ArrayList<>(arrayListSteps));
                arrayListSteps = new ArrayList<>();
            }

            int countPoints;
            BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, true);


            for (ArrayList<String[]> arrayList : arrayListLegs) {
                lstLatLngsInner = new ArrayList<>();
                for (String[] poliline : arrayList) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.BLACK);
                    options.width(width + 12);
                    options.startCap(new RoundCap());
                    options.endCap(new RoundCap());
                    options.geodesic(true);
                    options.addAll(PolyUtil.decode(poliline[0]));
                    options.zIndex(2);
                    countPoints = options.getPoints().size();
                    LatLng latLngStart = options.getPoints().get(0);
                    LatLng latLngFinish = options.getPoints().get(countPoints - 1);

                    lstLatLngsInner.add(PolyUtil.decode(poliline[0]));

                    latLngs.add(latLngStart);
                    latLngs.add(latLngFinish);

                    mMap.addMarker(new MarkerOptions().position(latLngStart).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).title("Підказка ")
                            .snippet(Html.fromHtml(poliline[1]).toString().replaceAll("\n\n", "\n ").replaceAll("\n ", "\n")));

                    mMap.addPolyline(options);
                    options.width(width + 8);
                    options.zIndex(3);
                    options.startCap(new RoundCap());
                    options.endCap(new RoundCap());
                    options.color(Color.rgb(0, 179, 253));
                    mMap.addPolyline(options);

                }
                lstLatLngsUpper.add(lstLatLngsInner);
            }

            mGeoDataClient.getPlaceById(place_ids.toArray(new String[0])).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place[] myPlace = new Place[countPlaceIds];
                        for (int i = 0; i < countPlaceIds; i++) {
                            myPlace[i] = places.get(i);
                            // DEBUG
//                        Log.i(TAG, "Place found(debug): " + myPlace[i].getName() + " " + myPlace[i].getLatLng().latitude + "," + myPlace[i].getLatLng().longitude +
//                                " | \n" + myPlace[i].getAddress() + " |  " + myPlace[i].getAttributions() + " |  " + myPlace[i].getLocale() + " |  "
//                                + myPlace[i].getPlaceTypes() + " |  " + myPlace[i].getPriceLevel() + " |  " + myPlace[i].getPhoneNumber());
                        }
                        BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white);
                        Bitmap waypointMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 30, 30, true);

                        bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.start_marker);
                        Bitmap middleMarkerStart = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 3, (int) Math.round((height / 0.67 * 1.13) * 3), true);

                        bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.finish_marker);
                        Bitmap middleMarkerFinish = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 3, (int) Math.round((height / 2.22 * 2.54) * 3), true);

                        countAvgRouteTime = 0;

                        for (int i = 0; i < countPlaceIds; i++) {
                            countAvgRouteTime += average_duration.get(i);
                            mMap.addMarker(new MarkerOptions().zIndex(5).position(myPlace[i].getLatLng()).snippet("Графік роботи : " + working_hours.get(i) +
                                    "\nТривалість екскурсії: ≈ " + calculateTime(average_duration.get(i) * 60) + //значення average_duration повертається в хв, а функція приймає значення секкунд, тому множимо на 60
                                    "\nЦіна: "  + prices.get(i) + " грн.")
                                    .title(myPlace[i].getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(waypointMarker)).anchor(0.5f, 0.5f));
                        }

                        IconGenerator iconFactory = new IconGenerator(mContext);
                        iconFactory.setColor(Color.argb(142, 3, 169, 245));
                        iconFactory.setTextAppearance(mContext, R.style.Text_BlackBold);


                        List<List<LatLng>> latLngsMiddle = new ArrayList<>();
                        List<LatLng> latLngsInner;
                        for (int i = 0; i < lstLatLngsUpper.size(); i++) {
                            latLngsInner = new ArrayList<>();
                            for (int j = 0; j < lstLatLngsUpper.get(i).size(); j++) {
                                latLngsInner.addAll(lstLatLngsUpper.get(i).get(j));
                            }
                            latLngsMiddle.add(latLngsInner);
                        }

                        countRouteDistance = 0;

                        arrayListLegsDistanceCount = arrayListLegsDistance.size();
                        mDistances = new Marker[arrayListLegsDistanceCount];
                        int i = 1;
                        if (arrayListLegsDistanceCount == 1)
                            i = 0;
                        for (; i < arrayListLegsDistanceCount; i++) {
                            markerOptions = new MarkerOptions().snippet("distance($%code#1)")
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Відстань: " + calculateDistance(Long.valueOf((String) arrayListLegsDistance.get(i)[0])) +
                                            "\nЧас: " + calculateTime(Long.valueOf((String) arrayListLegsDistance.get(i)[1])))))
                                    .position(findHalfRoute(latLngsMiddle.get(i), Double.valueOf((String) arrayListLegsDistance.get(i)[0]) / 2))
                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                            mDistances[i] = mMap.addMarker(markerOptions);
                            countRouteDistance += Long.valueOf((String) arrayListLegsDistance.get(i)[0]);
                        }

                        mMap.addMarker(new MarkerOptions().snippet("distance($%code#1)").position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerStart)));
                        mMap.addMarker(new MarkerOptions().snippet("distance($%code#1)").position(latLngs.get(latLngs.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerFinish)));

                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngs.get(0), DEFAULT_ZOOM), 50, null);

                        assert places != null;
                        places.release();
                    } else {
                        Log.e(TAG, "Place not found.");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }

    @Override
    public void onCameraMove() {
        if (mMap.getCameraPosition().zoom < 16) {
            for (int i = 1; i < arrayListLegsDistanceCount; i++) {
                if (mDistances.length > 0) {
                    if (mDistances[i].isVisible())
                        mDistances[i].setVisible(false);
                }
            }
        }
        if (mMap.getCameraPosition().zoom >= 16) {
            for (int i = 1; i < arrayListLegsDistanceCount; i++) {
                if (mDistances.length > 0) {
                    if (!mDistances[i].isVisible())
                        mDistances[i].setVisible(true);
                }
            }
        }
    }

    private static LatLng findHalfRoute(List<LatLng> latLngsMiddle, double halfDistance) {
        double mDistance = 0;
        LatLng halfPoint = latLngsMiddle.get(0);

        for (int i = 0; i < latLngsMiddle.size() - 1; i++) {
            if (mDistance < halfDistance) {
                mDistance += SphericalUtil.computeDistanceBetween(latLngsMiddle.get(i), latLngsMiddle.get(i + 1));
            }
            if (mDistance > halfDistance) {
                double distanceBetween = SphericalUtil.computeDistanceBetween(latLngsMiddle.get(i), latLngsMiddle.get(i + 1));
                double dif = distanceBetween - (mDistance - halfDistance);
                double fraction = 100 / mDistance * dif / 100;
                halfPoint = SphericalUtil.interpolate(latLngsMiddle.get(i), latLngsMiddle.get(i + 1), fraction);

                break;
            }
        }
        return halfPoint;
    }

    private static String calculateDistance(long metres) {
        long m = metres % 1000;
        long km = metres % 10000 / 1000;

        if (km == 0)
            return m + " м.";
        else if (km > 0 && m > 0)
            return km + " км. " + m + " м.";
        else return km + " км.";
    }

    private static String calculateTime(long seconds) {
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;

        if (hours == 0 && minutes == 0)
            return 1 + " хв.";
        else if (hours == 0)
            return minutes + " хв.";
        else if (hours > 0 && minutes > 0)
            return hours + " год. " + minutes + " хв.";
        else return hours + " год.";
    }

    public String getRouteDuration() {
        return calculateDistance(countRouteDistance);
    }

    public String getRouteDistance() {
        return calculateTime(countAvgRouteTime);
    }


}
