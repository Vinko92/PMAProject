package pma.vinko.legendtracker.model;

import android.graphics.Bitmap;

/**
 * Created by Vinko on 3.9.2016..
 */
public class Spell {

    private String name;
    private String description;
    private Bitmap image;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
