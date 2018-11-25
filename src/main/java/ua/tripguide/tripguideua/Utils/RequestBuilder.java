package ua.tripguide.tripguideua.Utils;

import com.google.android.gms.maps.model.LatLng;

import ua.tripguide.tripguideua.BuildConfig;

public class RequestBuilder {
    private StringBuilder url;

    public String buildUrl(LatLng[] latLngs) {

        latLngs = sortLatLng(latLngs);

        if (latLngs != null) {
            int countLatLngs = latLngs.length;
            url = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/directions/json?")
                    .append("origin=").append(latLngs[0].latitude).append(",").append(latLngs[0].longitude)
                    .append("&destination=").append(latLngs[countLatLngs - 1].latitude).append(",").append(latLngs[countLatLngs - 1].longitude);
            if (countLatLngs > 2) {
                url.append("&waypoints=optimize:true");
                for (int i = 0; i < countLatLngs - 1; i++) {
                    url.append("|").append(latLngs[i].latitude).append(",").append(latLngs[i].longitude);
                }
            }
//            url.append("&mode=TransitMode")
            url.append("&mode=walking")
                    .append("&key=" + BuildConfig.GoogleSecAPIKEY)
                    .append("&alternatives=false")
                    .append("&language=uk");

        }
        return url.toString();
    }

    private LatLng[] sortLatLng(LatLng[] latLngs) {
        int count = latLngs.length;
        int index = 1;

        double[] distances = new double[count - 1];

//        AB = √(xb - xa)**2 + (yb - ya)**2 - формула дистанції
        for (int i = 0; i < count - 1; i++) {
            distances[i] = Math.sqrt(Math.pow(latLngs[0].latitude - latLngs[i + 1].latitude, 2) + Math.pow(latLngs[0].longitude - latLngs[i + 1].longitude, 2));
        }

        double temp = distances[0];
        for (int i = 1; i < count-1; i++) {
            if (temp < distances[i]) {
                temp = distances[i];
                index = i + 1;
            }
        }
            LatLng tempLatLng = latLngs[count-1];
            latLngs[count-1] = latLngs[index];
            latLngs[index] = tempLatLng;

        return latLngs;
    }

}
