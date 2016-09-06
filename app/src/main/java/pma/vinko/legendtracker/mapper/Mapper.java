/*package pma.vinko.legendtracker.mapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import pma.vinko.legendtracker.model.Champion;
import pma.vinko.legendtracker.model.Spell;

/**
 * Created by Vinko on 3.9.2016..
 */
/*
public class Mapper  {

    public static Champion MapChampion(JSONObject json) throws JSONException {

        Champion champion = new Champion();
        champion.setName(json.getString("name"));
        champion.setTitle(json.getString("title"));
        champion.setId(json.getInt("id"));

        JSONObject info = json.getJSONObject("info");
        champion.setAttack(info.getInt("attack"));
        champion.setDefense(info.getInt("defense"));
        champion.setDifficulty(info.getInt("difficulty"));
        champion.setMagic(info.getInt("magic"));




    }

    public static Spell mapSpell(JSONObject json){

        Spell spell = new Spell();

    }
}

    private  void init(){

        InstantiationException {
            // Get the class name of this instance's type.
            ParameterizedType pt
                    = (ParameterizedType) getClass().getGenericSuperclass();
            // You may need this split or not, use logging to check
            String parameterClassName
                    = pt.getActualTypeArguments()[0].toString().split("\\s")[1];
            // Instantiate the Parameter and initialize it.
            try {
                instance = (T) Class.forName(parameterClassName).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }



}
*/
