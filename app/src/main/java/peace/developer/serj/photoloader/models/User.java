package peace.developer.serj.photoloader.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User {
    private int id;
    private String name;
    private List<Photo> listPhotos;
    private List<Album> listAlbums;

    public User (int id, String name){
        this.id = id;
        this.name = name;
        this.listPhotos = new ArrayList<>();
        listAlbums = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Photo> getPhotos() {
        return listPhotos;
    }

    public List<Album> getListAlbums() {
        return listAlbums;
    }
}
