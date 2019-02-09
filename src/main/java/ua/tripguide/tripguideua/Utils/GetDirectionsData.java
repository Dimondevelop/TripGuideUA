package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
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

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    private GoogleMap mMap;
    private ArrayList<RouteObjectsInfo> lstRouteObjectsInfos;
    private LatLng startLatLng, endLatLng;

    private ArrayList<LatLng> latLngs = new ArrayList<>();
    private int lstSize;
    private int countPlaceIds;
    private String data = "";

    private Context mContext;

    private ArrayList<String> place_ids = new ArrayList<>();
    private ArrayList<String> working_hours = new ArrayList<>();
    private ArrayList<Integer> average_duration = new ArrayList<>();

    private List<List<List<LatLng>>> lstLatLngsUpper = new ArrayList<>();
    private List<List<LatLng>> lstLatLngsInner;

    private int height = 15;
    private int width = 15;
    private int DEFAULT_ZOOM;

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

//        if (s.length() > 4000) {
//            Log.v("SOFT ", "sb.length = " + s.length());
//            int chunkCount = s.length() / 4000;     // integer division
//            for (int i = 0; i <= chunkCount; i++) {
//                int max = 4000 * (i + 1);
//                if (max >= s.length()) {
//                    Log.v("SOFT ", "chunk " + i + " of " + chunkCount + ":" + s.substring(4000 * i));
//                } else {
//                    Log.v("SOFT ", "chunk " + i + " of " + chunkCount + ":" + s.substring(4000 * i, max));
//                }
//            }
//        } else {
//            Log.v("SOFT ", s);
//        }

        GeoDataClient mGeoDataClient = Places.getGeoDataClient(mContext);

        try {
            JSONObject jsonObjectResponse = new JSONObject(s);
//            JSONArray jsonArrayGeocodedWaypoints = jsonObjectResponse.getJSONArray("geocoded_waypoints");
//
//            int countWaypoints = jsonArrayGeocodedWaypoints.length();
//            String[] placeIds = new String[countWaypoints];
//            for (int i = 0; i < countWaypoints; i++) {
//                placeIds[i] = jsonArrayGeocodedWaypoints.getJSONObject(i).getString("place_id");
//            }

            JSONArray jsonArrayRoutes = jsonObjectResponse.getJSONArray("routes");
            JSONArray jsonArrayLegs = jsonArrayRoutes.getJSONObject(0).getJSONArray("legs");
            int countJsonArrayLegs = jsonArrayLegs.length();

            ArrayList<ArrayList<String[]>> arrayListLegs = new ArrayList<>();
            final ArrayList<Object[]> arrayListLegsDistance = new ArrayList<>();
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

//                    j = countPoints + j;
//                    for (int p = 0; p < countPoints; p++) {
//                        latLngs.add(PolyUtil.decode(poliline[0]).get(p));
//                        if (p == Math.round(countPoints/2)){
//                            mMap.addMarker(new MarkerOptions().position(options.getPoints().get(p)));
//                        }
//                    }

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
//                mMap.addMarker(new MarkerOptions().position(latLngs.get(i)));
            }


//            for (int j = 0; j < arrayListLegs.size(); j++) {
//                for (int t = 0; t < arrayListLegs.get(j).size(); t++) {
//                    if (t == 0) {
//                        System.out.println("[" + j + "]\n\t[" + t + "] " + arrayListLegs.get(j).get(t)[0]);
//                    } else {
//                        System.out.println("\t[" + t + "] " + arrayListLegs.get(j).get(t)[0]);
//                    }
//                }
//            }

//            for (int i = 0; i < lstLatLngsUpper.size(); i++) {
//                for (int j = 0; j < lstLatLngsUpper.get(i).size(); j++) {
//                    if (j == 0)
//                        System.out.println("\ni[" + i + "] \n");
//
//                    System.out.println("\tj[" + j + "] \n");
//                    for (int t = 0; t < lstLatLngsUpper.get(i).get(j).size(); t++){
//                        System.out.println("\t\tt[" + t + "] {" + lstLatLngsUpper.get(i).get(j).get(t) + "}");
//                    }
//                }
//            }


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

                        int avg_count = 0;

                        for (int i = 0; i < countPlaceIds; i++) {
                            avg_count += average_duration.get(i);
                            mMap.addMarker(new MarkerOptions().zIndex(5).position(myPlace[i].getLatLng()).snippet("Графік роботи : " + working_hours.get(i) +
                                    "\nТривалість екскурсії: ≈ " + calculateTime(average_duration.get(i)*60)) //значення average_duration повертається в хв, а функція приймає значення секкунд, тому множимо на 60
                                    .title(myPlace[i].getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(waypointMarker)).anchor(0.5f, 0.5f));
                        }

                        IconGenerator iconFactory = new IconGenerator(mContext);
                        iconFactory.setColor(Color.argb(142, 3, 169, 245));
                        iconFactory.setTextAppearance(mContext, R.style.Text_BlackBold);


                        List<List<LatLng>> latLngsMiddle = new ArrayList<>();
                        List<LatLng>  latLngsInner;
                        for (int i = 0; i < lstLatLngsUpper.size(); i++) {
                            latLngsInner = new ArrayList<>();
                            for (int j = 0; j < lstLatLngsUpper.get(i).size(); j++) {
                                latLngsInner.addAll(lstLatLngsUpper.get(i).get(j));
                            }
                            latLngsMiddle.add(latLngsInner);
                        }

//                        for (int i = 0; i < lstLatLngsUpper.size(); i++) {
//                            for (int j = 0; j < lstLatLngsUpper.get(i).size(); j++) {
//                                latLngsMiddle.addAll(lstLatLngsUpper.get(i).get(j));
//                            }
//                        }


                        MarkerOptions markerOptions;
                        for (int i = 1; i < arrayListLegsDistance.size(); i++) {

                            markerOptions = new MarkerOptions().snippet("distance($%code#1)")
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("Відстань: " + calculateDistance(Long.valueOf((String) arrayListLegsDistance.get(i)[0])) +
                                            "\nЧас: " + calculateTime(Long.valueOf((String) arrayListLegsDistance.get(i)[1])))))
                                    .position(findHalfRoute(latLngsMiddle.get(i),Double.valueOf((String) arrayListLegsDistance.get(i)[0])/2))
                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//                                    .anchor(iconFactory.getAnchorU()-0.1f, iconFactory.getAnchorV()-0.01f);
                            mMap.addMarker(markerOptions);
                        }

                        mMap.addMarker(new MarkerOptions().snippet("distance($%code#1)").position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerStart)));
                        mMap.addMarker(new MarkerOptions().snippet("distance($%code#1)").position(latLngs.get(latLngs.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerFinish)));

//                        double fullDistance = 380;
//
//                        LatLng halfPoint = findHalfRoute(latLngsMiddle,fullDistance);
//
//                        markerOptions = new MarkerOptions().snippet("distance($%code#1)")
//                                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("MiddlePoint")))
//                                .position(halfPoint)
////                                    .position(SphericalUtil.interpolate(myPlace[i - 1].getLatLng(), myPlace[i].getLatLng(), 0.5))
//                                .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//                        mMap.addMarker(markerOptions);



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


    private static LatLng findHalfRoute (List<LatLng> latLngsMiddle, double halfDistance){
        double mDistance = 0;
        LatLng halfPoint = latLngsMiddle.get(0);

        for (int i = 1; i < latLngsMiddle.size();i++) {
            if (mDistance < halfDistance) {
                mDistance += SphericalUtil.computeDistanceBetween(latLngsMiddle.get(i - 1),latLngsMiddle.get(i));
            }
            if (mDistance > halfDistance){
                double distanceBetween = SphericalUtil.computeDistanceBetween(latLngsMiddle.get(i - 1),latLngsMiddle.get(i));
                double dif = distanceBetween-(mDistance - halfDistance);
                double fraction = 100/mDistance*dif/100;
                halfPoint = SphericalUtil.interpolate(latLngsMiddle.get(i - 1),latLngsMiddle.get(i), fraction);
//                halfPoint = findMiddlePoint(latLngsMiddle.get(i - 1),latLngsMiddle.get(i),mDistance,halfDistance);
                //DEBUG
//                mDistance -= SphericalUtil.computeDistanceBetween(latLngsMiddle.get(i),halfPoint); //Дистанція до повертаємої точки
//                System.out.println("mDistance " + mDistance + "\nhalfDistance " + halfDistance + "\ndistanceBetween " + distanceBetween+"\ndif "+ dif+"\nfraction " + fraction);
                break;
            }
        }
        return halfPoint;
    }

//    private static LatLng findMiddlePoint (LatLng l1, LatLng l2, double distanceToL2, double fullDistance) {
//        double distanceBetween = SphericalUtil.computeDistanceBetween(l1,l2); //200
//        double distanceToL1 = distanceToL2 - distanceBetween;
//        double dist = distanceToL1+distanceBetween/2;
//        LatLng half = SphericalUtil.interpolate(l1, l2, 0.1);
//        if (fullDistance > dist){
//            return half;
//        } else {
//            l2 = half;
//           return findMiddlePoint(l1,l2,distanceToL1,fullDistance);
//        }
//    }

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

        if (hours == 0 && minutes == 0 )
            return 1 + " хв.";
        else if (hours == 0)
            return minutes + " хв.";
        else if (hours > 0 && minutes > 0)
            return hours + " год. " + minutes + " хв.";
        else return hours + " год.";
    }


}
