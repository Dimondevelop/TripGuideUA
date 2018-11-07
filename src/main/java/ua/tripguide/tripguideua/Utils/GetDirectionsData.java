package ua.tripguide.tripguideua.Utils;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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

            for (ArrayList<String> arrayList:arrayListLegs) {
                for (String poliline:arrayList) {
                    PolylineOptions options = new PolylineOptions();
                    options.color(Color.BLUE);
                    options.width(10);
                    options.addAll(PolyUtil.decode(poliline));

                    mMap.addPolyline(options);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//
//        try {
//            JSONObject jsonObject = new JSONObject(s);
//            JSONArray jsonArraySteps = jsonObject.getJSONArray("routes")
//                    .getJSONObject(0).getJSONArray("legs")
//                    .getJSONObject(0).getJSONArray("steps");
//
//            int count_jsonArray = jsonArraySteps.length();
//            String[] poliline_array = new String[count_jsonArray];
//
//            JSONObject jsonObject_continue;
//
//            for (int j = 0; j < count_jsonArray; j++) {
//                jsonObject_continue = jsonArraySteps.getJSONObject(j);
//
//                String polygone = jsonObject_continue.getJSONObject("polyline").getString("points");
//
//                poliline_array[j] = polygone;
//            }
//
//            int count_poliline_array = poliline_array.length;
//
//            for (int i = 0; i < count_poliline_array; i++) {
//                PolylineOptions options2 = new PolylineOptions();
//                options2.color(Color.BLUE);
//                options2.width(10);
//                options2.addAll(PolyUtil.decode(poliline_array[i]));
//
//                mMap.addPolyline(options2);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        super.onPostExecute(s);
    }
}
