package ua.tripguide.tripguideua.Models;

public class City {

    private int id;
    private String name;
    private String Thumbnail;

    public City() {
    }

    public City(int id, String name, String thumbnail) {
        this.id = id;
        this.name = name;
        Thumbnail = thumbnail;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getThumbnail() {
        return Thumbnail;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setThumbnail(String thumbnail) {
        Thumbnail = thumbnail;
    }
}
