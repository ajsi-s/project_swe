package at.technikum.mrp;

// This class stores a rating.
public class Rating {

    private User user;
    private MediaEntry media;
    private int stars;

    public Rating(User user, MediaEntry media, int stars) {
        this.user = user;
        this.media = media;
        this.stars = stars;
    }

    public User getUser() {
        return user;
    }

    public MediaEntry getMedia() {
        return media;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }

    public String getInfo() {
        return user.getUsername() + " rated " + media.getTitle() + " with " + stars + " stars.";
    }
}
