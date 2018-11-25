package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.text.Html;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
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

import ua.tripguide.tripguideua.R;

public class GetDirectionsData extends AsyncTask<Object, String, String> {

    private GoogleMap mMap;
    private String url;
    private LatLng startLatLng, endLatLng;

    private HttpURLConnection httpURLConnection = null;
    String data = "";
    InputStream inputStream = null;
    Context mContext;

    public GetDirectionsData(Context c) {
        this.mContext = c;
    }

    @Override
    protected String doInBackground(Object... params) {

        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        startLatLng = (LatLng) params[2];
        endLatLng = (LatLng) params[3];

        try {
            URL myurl = new URL(url);
            httpURLConnection = (HttpURLConnection) myurl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
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

        try {
            JSONObject jsonObjectResponse = new JSONObject(s);
            JSONArray jsonArrayGeocodedWaypoints = jsonObjectResponse.getJSONArray("geocoded_waypoints");

            int countWaypoints = jsonArrayGeocodedWaypoints.length();
            String[] placeIds = new String[countWaypoints];
            for (int i = 0; i < countWaypoints; i++){
                placeIds[i] =  jsonArrayGeocodedWaypoints.getJSONObject(i).getString("place_id");
            }

            JSONArray jsonArrayRoutes = jsonObjectResponse.getJSONArray("routes");

            JSONArray jsonArrayLegs = jsonArrayRoutes.getJSONObject(0).getJSONArray("legs");
            int countJsonArrayLegs = jsonArrayLegs.length();

            ArrayList<ArrayList<String[]>> arrayListLegs = new ArrayList<>();
            ArrayList<String[]> arrayListSteps = new ArrayList<>();

            LatLng[] latLngsWaypoints = new LatLng[countJsonArrayLegs];
            JSONArray[] jsonArraySteps = new JSONArray[countJsonArrayLegs];

            JSONObject jsonObjectInSteps;
            for (int i = 0; i < countJsonArrayLegs; i++) {
                latLngsWaypoints[i] = new LatLng(Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("start_location").getString("lat")),
                                                Float.valueOf(jsonArrayLegs.getJSONObject(i).getJSONObject("start_location").getString("lng")));

                jsonArraySteps[i] = jsonArrayLegs.getJSONObject(i).getJSONArray("steps");
                for (int j = 0; j < jsonArraySteps[i].length(); j++) {
                    jsonObjectInSteps = jsonArraySteps[i].getJSONObject(j);

                    String polygone = jsonObjectInSteps.getJSONObject("polyline").getString("points");
                    String html_instructions = jsonObjectInSteps.getString("html_instructions");


                    arrayListSteps.add(new String[]{polygone,html_instructions});
                }
                arrayListSteps = new ArrayList<>(arrayListSteps);
                arrayListLegs.add(arrayListSteps);

            }

            int countPoints;
            int height = 12;
            int width = 12;
            BitmapDrawable bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_small_white2);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width, height, false);

//            bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_middle_bagel);
//            Bitmap middleMarkerStart = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 2, height * 2, false);

            bitmapdraw = (BitmapDrawable) mContext.getResources().getDrawable(R.drawable.circle_middle_black);
            Bitmap middleMarkerFinish = Bitmap.createScaledBitmap(bitmapdraw.getBitmap(), width * 2+12, height * 2+12, false);

            int i = 0;
            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (ArrayList<String[]> arrayList : arrayListLegs) {
                for (String[] poliline : arrayList) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.BLACK);
                    options.width(width+12);
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

                    if (!poliline.equals(arrayList.get(0))) {
                        mMap.addMarker(new MarkerOptions().position(latLngStart).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet(String.valueOf(countPoints))
                                .title(Html.fromHtml(poliline[1]).toString()));
                    }
                    mMap.addPolyline(options);
                    options.width(width+8);
                    options.zIndex(3);
                    options.startCap(new RoundCap());
                    options.endCap(new RoundCap());
                    options.color(Color.rgb(0,179,253));
                    mMap.addPolyline(options);
                }
//                if (i != 0){
//                    mMap.addMarker(new MarkerOptions().position(latLngsWaypoints[i]).title(latLngsWaypoints[i].latitude + " " + latLngsWaypoints[i].longitude));
//                }
//                i++;
            }
//            mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerStart)).anchor(0.5f, 0.5f));
            mMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size() - 1)).icon(BitmapDescriptorFactory.fromBitmap(middleMarkerFinish)).anchor(0.5f, 0.5f));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onPostExecute(s);
    }
}
