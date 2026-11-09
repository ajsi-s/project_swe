package at.technikum.mrp;

// This is the base class for the media objects.
public abstract class MediaEntry implements Rateable {

    // The fields are private. I use methods to access them.
    private String title;
    private String genre;
    private int id;

    // This value is shared by all MediaEntry objects.
    private static int creationCount = 0;

    // Constructor to give the object its first values.
    public MediaEntry(String title, String genre) {
        this.title = title;
        this.genre = genre;
        creationCount++;
    }

    // Getter to read the private field.
    public String getTitle() {
        return title;
    }

    // Setter to change the private field.
    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    // This is a normal method of the object.
    public String getInfo() {
        return title + " - " + genre;
    }

    // This method belongs to the class.
    public static int getCreationCount() {
        return creationCount;
    }

    // The subclasses have to implement this method.
    public abstract String getType();
}
