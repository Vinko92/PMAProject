package pma.vinko.legendtracker.helpers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.asynctasks.GetImage;

/**
 * Created by Vinko on 11.9.2016..
 */
public class ViewBuilder {

    public static void populateTable(HashMap<String,String> names, final Context activity, final Class clazz, TableLayout tl,
                                     HashMap<String, String> images, String type){

        names = sort(names);

        for(String key : names.keySet()) {
            final String copyKey = key;
            TableRow tr = createTableRow(activity);

            if(type != null) {

                ImageView imageView = new ImageView(activity);
                switch(type){
                    case "champion":
                        imageView.setImageBitmap(CacheHelper.championImages.get(key));
                        break;
                    case "item":
                        imageView.setImageBitmap(CacheHelper.itemImages.get(key));
                        break;
                    case "rune":
                        imageView.setImageBitmap(CacheHelper.runeImages.get(key));
                        break;
                    case "spell":
                        imageView.setImageBitmap(CacheHelper.spellImages.get(key));
                        break;
                }

                 tr.addView(imageView);
            }

            TextView textView = new TextView(activity);
            textView.setTextColor(Color.parseColor("#ffffff"));
            textView.setBackgroundDrawable(ContextCompat.getDrawable(activity,R.drawable.cell));
            textView.setText(names.get(key));
            tr.addView(textView);

            Button button = new Button(activity);
            button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            //button.setRight(0);
            button.setBackgroundDrawable(ContextCompat.getDrawable(activity,R.drawable.mybutton));
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setText("Details");

            if(clazz != null) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, clazz);
                        Bundle b = new Bundle();
                        b.putString("id", copyKey);
                        intent.putExtras(b);
                        activity.startActivity(intent);
                    }
                });
            }

            tr.addView(button);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }

    }

    private static HashMap<String, String> sort(HashMap<String,String> passedMap){

        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<String> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, String> sortedMap =
                new LinkedHashMap<>();

        Iterator<String> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            String val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                String comp1 = passedMap.get(key);
                String comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }

    private static TableRow createTableRow(Context activity){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow tr = new TableRow(activity);
        tr.setLayoutParams(lp);
        tr.setPadding(0,15,0,15);
        return tr;
    }
}
