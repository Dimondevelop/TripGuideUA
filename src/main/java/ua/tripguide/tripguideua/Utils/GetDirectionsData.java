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
import com.google.maps.android.ui.BubbleIconFactory;
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
                arrayListLegsDistance.add(new Object[]{distanse, duration,latLngsWaypoints});
                arrayListSteps = new ArrayList<>(arrayListSteps);
                arrayListLegs.add(arrayListSteps);
            }


            int countPoints;
            BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, true);


            final ArrayList<LatLng[]> lstLatLngsMiddle = new ArrayList<>();
            LatLng[] latLngsMiddle;

            int i = 0;
            for (ArrayList<String[]> arrayList : arrayListLegs) {

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

//                mMap.addMarker(new MarkerOptions().position(latLngs.get(i)));
                i++;
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

                        for (int i = 0; i < countPlaceIds; i++) {
                            mMap.addMarker(new MarkerOptions().zIndex(5).position(myPlace[i].getLatLng()).snippet("Час роботи : " + working_hours.get(i))
                                    .title(myPlace[i].getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(waypointMarker)).anchor(0.5f, 0.5f));
                        }

                        IconGenerator iconFactory = new IconGenerator(mContext);
                        iconFactory.setColor(Color.argb(142,3,169,245));
                        iconFactory.setTextAppearance(mContext,R.style.Text_BlackBold);


                        MarkerOptions markerOptions;
                        for (int i = 1; i < arrayListLegsDistance.size(); i++) {
                            markerOptions = new MarkerOptions().snippet("distance($%code#1)")
                                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon("" + calculateDistance(Long.valueOf((String)arrayListLegsDistance.get(i)[0])) +
                                            "\n" + calculateTime(Long.valueOf((String)arrayListLegsDistance.get(i)[1])))))
                                    .position((LatLng)arrayListLegsDistance.get(i-1)[2])
//                                    .position(SphericalUtil.interpolate(myPlace[i - 1].getLatLng(), myPlace[i].getLatLng(), 0.5))
                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
//                                    .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                            mMap.addMarker(markerOptions);
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
    private static String calculateDistance(long metres) {
        long m = metres % 1000;
        long km = metres % 10000 / 1000;

        if (km == 0)
            return m+" м.";
        else if (km > 0 && m > 0)
            return km + " км. " + m + " м.";
        else return km + " км.";
    }

    private static String calculateTime(long seconds) {
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;

        if (hours == 0)
            return minutes+" хв.";
       else if (hours > 0 && minutes > 0)
           return hours + " год. " + minutes + " хв.";
       else return hours + " год.";
    }


}
