package ua.tripguide.tripguideua.Models;

import java.util.ArrayList;

public class Excursion {
    private int _id_excursion;
    private String name_excursion;
    private String objects_list;
    private int _id_city_excursion;
    private String type_excutsion;
    private ArrayList<RouteObjectsInfo> routeObjectsInfos;

    public Excursion(int _id_excursion, String name_excursion, String objects_list, int _id_city_excursion, String type_excutsion, ArrayList<RouteObjectsInfo> routeObjectsInfos) {
        this._id_excursion = _id_excursion;
        this.name_excursion = name_excursion;
        this.objects_list = objects_list;
        this._id_city_excursion = _id_city_excursion;
        this.type_excutsion = type_excutsion;
        this.routeObjectsInfos = routeObjectsInfos;
    }

    public int get_id_excursion() {
        return _id_excursion;
    }

    public void set_id_excursion(int _id_excursion) {
        this._id_excursion = _id_excursion;
    }

    public String getName_excursion() {
        return name_excursion;
    }

    public void setName_excursion(String name_excursion) {
        this.name_excursion = name_excursion;
    }

    public String getObjects_list() {
        return objects_list;
    }

    public void setObjects_list(String objects_list) {
        this.objects_list = objects_list;
    }

    public int get_id_city_excursion() {
        return _id_city_excursion;
    }

    public void set_id_city_excursion(int _id_city_excursion) {
        this._id_city_excursion = _id_city_excursion;
    }

    public String getType_excutsion() {
        return type_excutsion;
    }

    public void setType_excutsion(String type_excutsion) {
        this.type_excutsion = type_excutsion;
    }

    public ArrayList<RouteObjectsInfo> getRouteObjectsInfos() {
        return routeObjectsInfos;
    }

    public void setRouteObjectsInfos(ArrayList<RouteObjectsInfo> routeObjectsInfos) {
        this.routeObjectsInfos = routeObjectsInfos;
    }
}
