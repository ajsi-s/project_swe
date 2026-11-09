package at.technikum.mrp;

// Movie inherits from MediaEntry.
public class Movie extends MediaEntry {

    private int duration;

    public Movie(String title, String genre, int duration) {
        super(title, genre);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    // Movie has its own version of this method.
    // override this method replaces a method from a parent class or interface
    @Override
    public String getType() {
        return "Movie";
    }

    // Implementation of the interface method.
    @Override
    public String rateInfo() {
        return getTitle() + " can be rated from 1 to 5 stars.";
    }
}
