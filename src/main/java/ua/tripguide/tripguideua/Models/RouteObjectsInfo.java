package ua.tripguide.tripguideua.Models;

import com.google.android.gms.maps.model.LatLng;
//ROI (RouteObjectsInfo) - загальна модель екскурсійного об'єкту з обмеженим набором атрибутів, потрібних для прокладання маршруту
public class RouteObjectsInfo {
    private String place_id;
    private String title;
    private String working_hour;
    private int average_duration;
    private int price;
    private LatLng latLng;

    public RouteObjectsInfo(String place_id, String title, String working_hour, int average_duration, int price, LatLng latLng) {
        this.place_id = place_id;
        this.title = title;
        this.working_hour = working_hour;
        this.latLng = latLng;
        this.average_duration = average_duration;
        this.price = price;
    }

    public RouteObjectsInfo(RouteObjectsInfo routeObjectsInfo) {
        this.place_id = routeObjectsInfo.getPlace_id();
        this.title = routeObjectsInfo.title;
        this.working_hour = routeObjectsInfo.getWorking_hour();
        this.latLng = routeObjectsInfo.getLatLng();
        this.average_duration = routeObjectsInfo.getAverage_duration();
        this.price = routeObjectsInfo.getPrice();
    }

    public String getPlace_id() {
        return place_id;
    }
    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getWorking_hour() {
        return working_hour;
    }
    public void setWorking_hour(String working_hour) {
        this.working_hour = working_hour;
    }
    public LatLng getLatLng() {
        return latLng;
    }
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
    public int getAverage_duration() {
        return average_duration;
    }
    public void setAverage_duration(int average_duration) {
        this.average_duration = average_duration;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
}

