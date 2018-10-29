package ua.tripguide.tripguideua.Models;

public class ObjectList {
    private int id_object;
    private String name_object;
    private String thumbnail_object;
    private float coordinate_x;
    private float coordinate_y;
    private int _id_city_object;
    private String object_description;
    private String type_object;
    private String working_hours;

    public ObjectList(int id_object, String name_object, String thumbnail_object, float coordinate_x, float coordinate_y,
                      int _id_city_object, String object_description, String type_object, String working_hours) {
        this.id_object = id_object;
        this.name_object = name_object;
        this.thumbnail_object = thumbnail_object;
        this.coordinate_x = coordinate_x;
        this.coordinate_y = coordinate_y;
        this._id_city_object = _id_city_object;
        this.object_description = object_description;
        this.type_object = type_object;
        this.working_hours = working_hours;
    }

    public int getId_object() {
        return id_object;
    }

    public void setId_object(int id_object) {
        this.id_object = id_object;
    }

    public String getName_object() {
        return name_object;
    }

    public void setName_object(String name_object) {
        this.name_object = name_object;
    }

    public String getThumbnail_object() {
        return thumbnail_object;
    }

    public void setThumbnail_object(String thumbnail_object) {
        this.thumbnail_object = thumbnail_object;
    }

    public float getCoordinate_x() {
        return coordinate_x;
    }

    public void setCoordinate_x(float coordinate_x) {
        this.coordinate_x = coordinate_x;
    }

    public float getCoordinate_y() {
        return coordinate_y;
    }

    public void setCoordinate_y(float coordinate_y) {
        this.coordinate_y = coordinate_y;
    }

    public int get_id_city_object() {
        return _id_city_object;
    }

    public void set_id_city_object(int _id_city_object) {
        this._id_city_object = _id_city_object;
    }

    public String getObject_description() {
        return object_description;
    }

    public void setObject_description(String object_description) {
        this.object_description = object_description;
    }

    public String getType_object() {
        return type_object;
    }

    public void setType_object(String type_object) {
        this.type_object = type_object;
    }

    public String getWorking_hours() {
        return working_hours;
    }

    public void setWorking_hours(String working_hours) {
        this.working_hours = working_hours;
    }

//    @Override
//    public String toString() {
//        return  getCoordinate_x() + " " + getCoordinate_y();
//    }
}
