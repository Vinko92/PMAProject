package pma.vinko.legendtracker.model;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Vinko on 3.9.2016..
 */
public class Champion {

    private int id;
    private String name;
    private String title;
    private Bitmap splashArt;
    ArrayList<Spell> spells = new ArrayList<Spell>();
    private int attack;
    private int defense;
    private int difficulty;
    private int magic;

    public Champion() {}

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Bitmap getSplashArt() {
        return splashArt;
    }

    public void setSplashArt(Bitmap splashArt) {
        this.splashArt = splashArt;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void setSpells(ArrayList<Spell> spells) {
        this.spells = spells;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMagic() {
        return magic;
    }

    public void setMagic(int magic) {
        this.magic = magic;
    }
}
