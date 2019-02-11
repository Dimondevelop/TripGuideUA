package ua.tripguide.tripguideua.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import ua.tripguide.tripguideua.Models.ObjectList;
import ua.tripguide.tripguideua.Models.RouteObjectsInfo;

public class UtilMethods {


    public static RouteObjectsInfo nearestObject(RouteObjectsInfo mROI, ArrayList<ObjectList> lstObjectBreakList) {

        int lstCount = lstObjectBreakList.size();
        int indexOfMin = 0;
        double[] distances = new double[lstCount];

        LatLng firstLatLng = mROI.getLatLng();
        LatLng[] breakLatLngs = new LatLng[lstCount];

        for (int i = 0; i < lstCount; i++) {
            breakLatLngs[i] = new LatLng(lstObjectBreakList.get(i).getCoordinate_x(),lstObjectBreakList.get(i).getCoordinate_y());
            distances[i] = SphericalUtil.computeDistanceBetween(firstLatLng, breakLatLngs[i]);
            if (distances[i] < distances[indexOfMin])
            {
                indexOfMin = i;
            }
        }


        return new RouteObjectsInfo(lstObjectBreakList.get(indexOfMin).getPlace_id(), lstObjectBreakList.get(indexOfMin).getName_object(),
                lstObjectBreakList.get(indexOfMin).getWorking_hours(), lstObjectBreakList.get(indexOfMin).getAverage_duration(),
                new LatLng(lstObjectBreakList.get(indexOfMin).getCoordinate_x(),lstObjectBreakList.get(indexOfMin).getCoordinate_y()));
    }


    public static String listToFormatString(ArrayList<String> strings) { //Додає всі елементи ArrayList в рядок, розділяючи їх комою та пробілом. Вкінці ставить крапку. Перща літера велика, всі остальні маленькі.
        StringBuilder string = new StringBuilder();
        for (String s : strings) {
            string.append(s).append(", ");
        }
        string.setLength(string.length() - 2);
        string.append(".");
        String str = String.valueOf(string);
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static boolean containsArray(String s, ArrayList<String> c) { //Перевіряє чи є в рядку хоч один з рядкових елементів ArrayList, якщо так - повертає true, якщо ні - false.
        for (String ss : c) {
            if (s.contains(ss)) return true;
        }
        return false;
    }


}
