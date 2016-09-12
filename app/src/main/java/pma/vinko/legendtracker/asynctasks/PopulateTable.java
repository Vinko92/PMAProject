package pma.vinko.legendtracker.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.activity.ChampionDetails;
import pma.vinko.legendtracker.helpers.StreamHelper;
import pma.vinko.legendtracker.helpers.ViewBuilder;

/**
 * Created by Vinko on 4.9.2016..
 */

public class PopulateTable extends AsyncTask<String, Void, Void> {

    private Context context;
    TableLayout tableLayout;
    private View rootView;
    private Class clazz;
    private String columns;
    private String content;
    private ProgressDialog progressDialog;
    private int substringLength;
    private boolean parseImages;
    private String type;

    public PopulateTable(Context context,View rootView, TableLayout tableLayout, Class clazz, String columns, int substringLength ) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rootView = rootView;
        this.clazz = clazz;
        this.columns = columns;
        this.substringLength = substringLength;

    }

    public PopulateTable(Context context,View rootView, TableLayout tableLayout, Class clazz, String columns, int substringLength,
                        boolean parseImages, String type) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rootView = rootView;
        this.clazz = clazz;
        this.columns = columns;
        this.substringLength = substringLength;
        this.parseImages = parseImages;
        this.type = type;
    }


    public PopulateTable(Context context,View rootView, TableLayout tableLayout, Class clazz, String columns ) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rootView = rootView;
        this.clazz = clazz;
        this.columns = columns;

    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please wait...");
        progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... urls) {
        String firstUrl = urls[0];

        try {
            InputStream firstStream = new java.net.URL(firstUrl).openStream();
            content = readStream(firstStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        HashMap<String,String> names = new HashMap<String,String>();
        HashMap<String,String> images = new HashMap<String,String>();

        try {
            JSONObject data = new JSONObject(content).getJSONObject("data");
            Iterator<String> keys = data.keys();

            while (keys.hasNext()) {
                //get champ details
                JSONObject champion = data.getJSONObject(keys.next());
                //add champ details
                if (champion.has("name")) {

                    StringBuilder value = new StringBuilder();

                    for(String column : columns.split(";")){
                        value.append(champion.getString(column));
                        value.append(" ");
                    }
                    String stringValue = value.toString();
                    if(substringLength > 0)
                    {
                        stringValue = value.length() > substringLength ? value.toString().substring(0,substringLength) + "..." : addSpaces(value.toString(), substringLength);
                    }
                    if("champion".equals(type)) names.put(champion.getString("key"),stringValue);
                    else names.put(champion.getString("id"),stringValue);



                    if(parseImages){
                        if("champion".equals(type)) images.put(champion.getString("key"), champion.getJSONObject("image").getString("full"));
                        else images.put(champion.getString("id"), champion.getJSONObject("image").getString("full"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ViewBuilder.populateTable(names,context,clazz, tableLayout, images, type);
        progressDialog.dismiss();
    }

    private String addSpaces(String value, int number){

        StringBuilder builder = new StringBuilder(value);

        for(int i = 0; i < number - value.length(); i++){
            builder.append(" ");
        }

        return builder.toString();
    }

    private TextView createTextView(String text){

        TableRow tr = createTableRow();
        TextView textView = new TextView(context);
        textView.setPadding(12,12,12,12);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // textView.setTextColor(Color.YELLOW);
        textView.setText(text);

        return textView;
    }

    private TableRow createTableRow(){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow tr = new TableRow(context);
        tr.setLayoutParams(lp);
        tr.setPadding(0,15,0,15);
        return tr;
    }

    @NonNull
    private String readStream(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line = "";
        StringBuilder result = new StringBuilder();

        while((line = reader.readLine()) != null){
            result.append(line);
        }

        stream.close();
        return  result.toString();
    }
}
