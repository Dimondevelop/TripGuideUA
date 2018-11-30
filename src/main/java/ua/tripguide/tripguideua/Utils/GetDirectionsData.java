package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
    private String data = "";

    private Context mContext;

    private String[] place_ids;
    private String[] working_hours;

    private int height = 12;
    private int width = 12;

    public GetDirectionsData(Context mContext, ArrayList<RouteObjectsInfo> lstRouteObjectsInfos) {
        this.mContext = mContext;
        this.lstRouteObjectsInfos = lstRouteObjectsInfos;
        this.lstSize = lstRouteObjectsInfos.size();

        place_ids = new String[lstSize];
        working_hours = new String[lstSize];

        for (int i = 0; i < lstSize; i++) {
            place_ids[i] = lstRouteObjectsInfos.get(i).getPlace_id();
            working_hours[i] = lstRouteObjectsInfos.get(i).getWorking_hour();
        }
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
            ArrayList<String[]> arrayListSteps = new ArrayList<>();

//            LatLng[] latLngsWaypoints = new LatLng[countJsonArrayLegs];
            JSONArray[] jsonArraySteps = new JSONArray[countJsonArrayLegs];

            JSONObject jsonObjectInSteps;
            for (int i = 0; i < countJsonArrayLegs; i++) {
//                latLngsWaypoints[i] = new LatLng(Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("start_location").getString("lat")),
//                        Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("start_location").getString("lng")));

                jsonArraySteps[i] = jsonArrayLegs.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < jsonArraySteps[i].length(); j++) {
                    jsonObjectInSteps = jsonArraySteps[i].getJSONObject(j);

                    String polygone = jsonObjectInSteps.getJSONObject("polyline").getString("points");
                    String html_instructions = jsonObjectInSteps.getString("html_instructions");


                    arrayListSteps.add(new String[]{polygone, html_instructions});
                }
                arrayListSteps = new ArrayList<>(arrayListSteps);
                arrayListLegs.add(arrayListSteps);

            }

            int countPoints;
            BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false);

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
                    i++;
                }

            }

            mGeoDataClient.getPlaceById(place_ids).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place[] myPlace = new Place[lstSize];
                        for (int i = 0; i < lstSize; i++) {
                            myPlace[i] = places.get(i);
                            // DEBUG
//                        Log.i(TAG, "Place found(debug): " + myPlace[i].getName() + " " + myPlace[i].getLatLng().latitude + "," + myPlace[i].getLatLng().longitude +
//                                " | \n" + myPlace[i].getAddress() + " |  " + myPlace[i].getAttributions() + " |  " + myPlace[i].getLocale() + " |  "
//                                + myPlace[i].getPlaceTypes() + " |  " + myPlace[i].getPriceLevel() + " |  " + myPlace[i].getPhoneNumber());
                        }

                        BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white);
                        Bitmap waypointMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), 30, 30, false);

                        bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_middle_bagel);
                        Bitmap middleMarkerStart = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 2, height * 2, false);

                        bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_middle_black);
                        Bitmap middleMarkerFinish = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 2 + 12, height * 2 + 12, false);
                        for (int i = 0; i < lstSize; i++) {
                            mMap.addMarker(new MarkerOptions().position(myPlace[i].getLatLng()).snippet("Час роботи : \n" + working_hours[i])
                                    .title(myPlace[i].getName().toString()).icon(BitmapDescriptorFactory.fromBitmap(waypointMarker)).anchor(0.5f, 0.5f));
                        }
                        mMap.addMarker(new MarkerOptions().title(myPlace[0].getName().toString()).snippet("Приємної екскурсії").position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerStart)).anchor(0.5f, 0.5f));
                        mMap.addMarker(new MarkerOptions().title(myPlace[lstSize - 1].getName().toString()).position(latLngs.get(latLngs.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerFinish)).anchor(0.5f, 0.5f));
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

}
