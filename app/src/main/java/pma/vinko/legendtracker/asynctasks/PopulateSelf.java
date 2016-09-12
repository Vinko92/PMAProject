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
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.helpers.StreamHelper;

/**
 * Created by Vinko on 4.9.2016..
 */

public class PopulateSelf extends AsyncTask<String, Void, Void> {

    Context context;
    TableLayout tableLayout;
    private View rootView;
    private String leagueStatsContent;
    private String championContent;
    private ProgressDialog progressDialog;

    public PopulateSelf(Context context,View rootView, TableLayout tableLayout) {
        this.context = context;
        this.tableLayout = tableLayout;
        this.rootView = rootView;

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
        String secondUrl = urls[1];

        try {
            InputStream firstStream = new java.net.URL(firstUrl).openStream();
            InputStream secondStream = new java.net.URL(secondUrl).openStream();
            leagueStatsContent = readStream(firstStream);
            championContent = readStream(secondStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        try {
            JSONObject json = new JSONObject(leagueStatsContent).getJSONArray("22191568").getJSONObject(0);
            JSONArray entries = json.getJSONArray("entries");

            TextView text = ((TextView) rootView.findViewById(R.id.playerNameDetails));
            text.setText(json.getString("name") + ", " +json.getString("tier"));

            for(int i = 0; i < entries.length(); i++){

                JSONObject entry = entries.getJSONObject(i);
                if(entry.getString("playerOrTeamName").toLowerCase().equals("skilledcex")){

                    String points = entry.getString("leaguePoints");
                    String division = entry.getString("division");
                    String losses = entry.getString("losses");
                    String wins = entry.getString("wins");

                    ((TextView)rootView.findViewById(R.id.division)).setText("Division: " + division);
                    ((TextView)rootView.findViewById(R.id.league)).setText("Points: " + points);
                    ((TextView)rootView.findViewById(R.id.wins)).setText("Wins: " + wins);
                    ((TextView)rootView.findViewById(R.id.losses)).setText("Losses: " + losses);

                    TableLayout tl = (TableLayout) rootView.findViewById(R.id.champsStatsTable);
                    HashMap<String,String> names = new HashMap<String,String>();

                    try {
                        JSONArray data = new JSONObject(championContent).getJSONArray("champions");

                        for(int j = 0; j < data.length(); j++){
                            JSONObject stats = data.getJSONObject(j);
                            JSONObject champStats = stats.getJSONObject("stats");
                            TableRow tr = createTableRow();
                            TextView textView = new TextView(context);
                            String champUrl = "https://global.api.pvp.net/api/lol/static-data/eune/v1.2/champion/"+ stats.getString("id") +"?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA"; //"http://ddragon.leagueoflegends.com/cdn/6.17.1/data/en_US/champion/" + championId  +" .json";
                            JSONObject champData = new JSONObject( new GetData(context).execute(champUrl).get());
                            tr.addView(createTextView(champData.getString("name")));
                            tr.addView(createTextView(champStats.getString("totalSessionsWon")));
                            tr.addView(createTextView(champStats.getString("totalSessionsLost")));
                            tr.addView(createTextView(champStats.getString("totalChampionKills")));
                            tr.addView(createTextView(champStats.getString("totalDeathsPerSession")));
                            tr.addView(createTextView(champStats.getString("totalAssists")));
                            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressDialog.dismiss();
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
