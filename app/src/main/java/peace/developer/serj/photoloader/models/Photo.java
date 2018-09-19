package peace.developer.serj.photoloader.models;

public class Photo {
    private int id;
    private String link;
    private String title;

    public Photo(int id, String link, String title) {
        this.id = id;
        this.link = link;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }
}
