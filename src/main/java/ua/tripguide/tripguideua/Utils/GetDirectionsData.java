package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
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
    Context c;

    public GetDirectionsData(Context c) {
        this.c = c;
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
            JSONArray jsonArrayRoutes = jsonObjectResponse.getJSONArray("routes");

            JSONArray jsonArrayLegs = jsonArrayRoutes.getJSONObject(0).getJSONArray("legs");
            int countJsonArrayLegs = jsonArrayLegs.length();

            ArrayList<ArrayList<String>> arrayListLegs = new ArrayList<>();
            ArrayList<String> arrayListSteps = new ArrayList<>();

            JSONArray[] jsonArraySteps = new  JSONArray[countJsonArrayLegs];
            JSONObject jsonObjectInSteps;
            for (int i = 0; i < countJsonArrayLegs; i++) {
                jsonArraySteps[i] = jsonArrayLegs.getJSONObject(i).getJSONArray("steps");
                int count_jsonArray = jsonArraySteps[i].length();

                for (int j = 0; j < count_jsonArray; j++){
                    jsonObjectInSteps = jsonArraySteps[i].getJSONObject(j);

                    String polygone = jsonObjectInSteps.getJSONObject("polyline").getString("points");

                    arrayListSteps.add(polygone);
                }
                arrayListSteps = new ArrayList<>(arrayListSteps);
                arrayListLegs.add(arrayListSteps);

            }

            int countPoints;
            int height = 15;
            int width = 15;
            BitmapDrawable bitmapdraw_small=(BitmapDrawable) c.getResources().getDrawable(R.drawable.circle_small_white);
            Bitmap b_small=bitmapdraw_small.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b_small, width, height, false);

            BitmapDrawable bitmapdraw=(BitmapDrawable) c.getResources().getDrawable(R.drawable.circle_middle_black);
            Bitmap b_middle=bitmapdraw.getBitmap();
            Bitmap middleMarker = Bitmap.createScaledBitmap(b_middle, width*3, height*3, false);


            ArrayList<LatLng> latLngs = new ArrayList<>();
            for (ArrayList<String> arrayList:arrayListLegs) {
                for (String poliline:arrayList) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.BLUE);
                    options.width(15);
                    options.startCap(new RoundCap());
                    options.endCap(new RoundCap());
                    options.geodesic(true);
                    options.addAll(PolyUtil.decode(poliline));

                    countPoints = options.getPoints().size();
                    LatLng latLngStart = options.getPoints().get(0);
                    LatLng latLngFinish = options.getPoints().get(countPoints-1);
                    latLngs.add(latLngStart);
                    latLngs.add(latLngFinish);

                    if (!poliline.equals(arrayList.get(0))){
                        mMap.addMarker(new MarkerOptions().position(latLngStart).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)).snippet(String.valueOf(countPoints))
                                .title(latLngStart.latitude + " " + latLngStart.longitude));
                    }
                    mMap.addPolyline(options);
                }
            }
            mMap.addMarker(new MarkerOptions().position(latLngs.get(0)).icon(BitmapDescriptorFactory.fromBitmap(middleMarker)).anchor(0.5f,0.5f));
            mMap.addMarker(new MarkerOptions().position(latLngs.get(latLngs.size()-1)).icon(BitmapDescriptorFactory.fromBitmap(middleMarker)).anchor(0.5f,0.5f));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onPostExecute(s);
    }
}
