package at.technikum.mrp;

// Series also inherits from MediaEntry.
public class Series extends MediaEntry {

    private int seasons;

    public Series(String title, String genre, int seasons) {
        super(title, genre);
        this.seasons = seasons;
    }

    public int getSeasons() {
        return seasons;
    }

    @Override
    public String getType() {
        return "Series";
    }

    @Override
    public String rateInfo() {
        return getTitle() + " can be rated from 1 to 5 stars.";
    }
}
