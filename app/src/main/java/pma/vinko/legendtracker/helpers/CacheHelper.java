package pma.vinko.legendtracker.helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by Vinko on 12.9.2016..
 */
public class CacheHelper {


    public enum CacheType{
        CHAMPION,
        ITEM,
        RUNE,
        SPELL
    };

    public static HashMap<String, Bitmap> championImages = null;
    public static HashMap<String, Bitmap> spellImages = null;
    public static HashMap<String, Bitmap> runeImages = null;
    public static HashMap<String, Bitmap> itemImages = null;
    public static String selfLeagueStats = null;
    public static String selfChampStats = null;

    public static void populate(CacheType type, HashMap<String, Bitmap> map ){
        switch (type){
            case CHAMPION:
                championImages = map;
                break;
            case ITEM:
                itemImages = map;
                break;
            case RUNE:
                runeImages = map;
                break;
            case SPELL:
                spellImages = map;
                break;
            default:
                break;
        }
    }

    public static void populateLeagueStats(String string) {
       selfLeagueStats = string;
    }

    public static void populateChampStats(String string) {
        selfChampStats = string;
    }





}
