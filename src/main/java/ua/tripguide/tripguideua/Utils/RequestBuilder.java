package ua.tripguide.tripguideua.Utils;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ua.tripguide.tripguideua.BuildConfig;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;

public class RequestBuilder {
    private StringBuilder url;

    public String buildUrl(ArrayList<RouteObjectsInfo> routeObjectsInfoList) {
        
        if (routeObjectsInfoList != null) {
            int size = routeObjectsInfoList.size();
            url = new StringBuilder()
                    .append("https://maps.googleapis.com/maps/api/directions/json?")
                    .append("origin=").append(routeObjectsInfoList.get(0).getLatLng().latitude).append(",").append(routeObjectsInfoList.get(0).getLatLng().longitude)
                    .append("&destination=").append(routeObjectsInfoList.get(size - 1).getLatLng().latitude).append(",").append(routeObjectsInfoList.get(size - 1).getLatLng().longitude);
            if (size > 2) {
                url.append("&waypoints=optimize:true");
                for (int i = 0; i < size - 1; i++) {
                    url.append("|").append(routeObjectsInfoList.get(i).getLatLng().latitude).append(",").append(routeObjectsInfoList.get(i).getLatLng().longitude);
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



}
