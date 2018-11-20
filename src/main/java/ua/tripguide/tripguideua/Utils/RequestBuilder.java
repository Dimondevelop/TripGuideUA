package ua.tripguide.tripguideua.Utils;

import com.google.android.gms.maps.model.LatLng;

import ua.tripguide.tripguideua.BuildConfig;

public class RequestBuilder {
    private StringBuilder url;

    public String buildUrl(LatLng[] latLngs){

        if (latLngs != null) {
            int countLatLngs = latLngs.length;
            url = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/directions/json?")
                    .append("origin=").append(latLngs[0].latitude).append(",").append(latLngs[0].longitude)
                    .append("&destination=").append(latLngs[countLatLngs - 1].latitude).append(",").append(latLngs[countLatLngs - 1].longitude);
            if (countLatLngs > 2) {
                url.append("&waypoints=").append(latLngs[1].latitude).append(",").append(latLngs[1].longitude);

                if (countLatLngs > 3)
                    for (int i = 1; i < countLatLngs - 1; i++) {
                        url.append("|").append(latLngs[i].latitude).append(",").append(latLngs[i].longitude);
                    }
            }

//            url.append("&mode=TransitMode")
            url.append("&mode=walking")
                    .append("&key=" + BuildConfig.GoogleSecAPIKEY)
                    .append("&alternatives=false");

        }
        return url.toString();
    }

}
