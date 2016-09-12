package pma.vinko.legendtracker.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.asynctasks.GetData;
import pma.vinko.legendtracker.asynctasks.GetImage;
import pma.vinko.legendtracker.helpers.CacheHelper;
import pma.vinko.legendtracker.helpers.PlayerHelper;
import pma.vinko.legendtracker.helpers.UrlBuilder;

public class LiveGamesActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;
    private ProgressDialog progressDialog;
    private  String summonerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_games);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Init layout
        setSpinner();
        setButton();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) < 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {

            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }


    public void onBackPressed() {
        Intent setIntent = new Intent(LiveGamesActivity.this,MainActivity.class);
        startActivity(setIntent);
        finish();
        return;

    }
    private void setSpinner(){
        spinner = (Spinner) findViewById(R.id.drpRegions);
        String[] regions = new String[]{ "EUNE","EUW","NA","BR","TR","JP","KR","LAN","LAS","OCE","PBE","RU","TR" };

        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, regions);
        spinner.setAdapter(arrayAdapter);
    }

    private void setButton() {
        button = (Button) findViewById(R.id.btnFetch);
        final TableLayout tl = (TableLayout) findViewById(R.id.gameDetails);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               tl.removeAllViews();
                EditText userInput = (EditText) findViewById(R.id.userInput);
                String url = UrlBuilder.BuildUrl("https://eune.api.pvp.net/api/lol/", spinner.getSelectedItem().toString(), "/v1.4/summoner/by-name/",
                        userInput.getText().toString(), "?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA");


                try {
                    JSONObject jsonData = new JSONObject(new GetData().execute(url).get());
                    summonerId = jsonData.getJSONObject(jsonData.keys().next()).getString("id");
                    String currenGameUrl = UrlBuilder.BuildUrl("https://eune.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/"
                            , "EUN1/", summonerId, "?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA");

                    String data = new GetData().execute(currenGameUrl).get();
                    parseData(data);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

        protected void parseData(String data){
            progressDialog = new ProgressDialog(LiveGamesActivity.this);
            progressDialog.setTitle("Please wait...");
            progressDialog.show();

            try {

                JSONObject json = new JSONObject(data);
                StringBuilder gameDetails = new StringBuilder();

                TableLayout tl = (TableLayout) findViewById(R.id.gameDetails);

                parseBannedChampions(json.getJSONArray("bannedChampions"),tl, 100);
                parseParticipants(json.getJSONArray("participants"), tl, 100);

                parseBannedChampions(json.getJSONArray("bannedChampions"),tl, 200);
                parseParticipants(json.getJSONArray("participants"), tl, 200);
            /*


                    //get champ details
                    final JSONObject jsonChampion = json.getJSONObject(keys.next());
                    //instantiate table row
                    TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                    TableRow tr = new TableRow(LiveGamesActivity.this);
                    tr.setLayoutParams(lp);

                    //add champ details
                    tr.addView(getTextView(jsonChampion, "name","title"));
                    tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
*/        } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            progressDialog.dismiss();
        }

        private void parseBannedChampions(JSONArray json, TableLayout tl, int team) throws JSONException {

            TableRow tr = createTableRow();
            tr.addView(createTextView("Bans: "));
            tr.setPadding(0,0,0,30);

            for(int i = 0; i < json.length(); i++) {

                JSONObject bannedChampion = json.getJSONObject(i);
                int teamId = bannedChampion.getInt("teamId");

                if(teamId == team) {
                    String championId = bannedChampion.getString("championId");

                    try {
                        tr.addView(getImage(championId));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        }


    private void parseParticipants(JSONArray json, TableLayout tl, int team) throws JSONException {

        TableRow teamTitle = createTableRow();
        teamTitle.addView(createTextView("Team " + team/100));
        teamTitle.setPadding(0,0,0,30);
        tl.addView(teamTitle);

        for(int i = 0; i < json.length(); i++) {
            TableRow tr = createTableRow();
            JSONObject participant = json.getJSONObject(i);
            int teamId = participant.getInt("teamId");

            if (team == teamId) {

                String championId = participant.getString("championId");
                try {
                    tr.addView(getImage(championId));
                    tr.addView(getTextView(participant, "summonerName"));
                    String playerLeague = PlayerHelper.getLeagueStats(participant.getString("summonerId"));
                    tr.addView("".equals(playerLeague) ? createTextView("GOLD III") : createTextView(playerLeague));
                    String kda = PlayerHelper.getKDA(participant.getString("summonerId"), championId);
                    tr.addView("".equals(kda) ? createTextView("  0   0") : createTextView(kda));
                    Thread.sleep(1000);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
            }
        }
    }


    private View getImage(String championId) throws ExecutionException, InterruptedException, JSONException {
            ImageView image = new ImageView(LiveGamesActivity.this);
            image.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            image.setPadding(10,0,10,0);
            image.setImageBitmap(CacheHelper.championImages.get(championId));
            return image;
        }


        private View getTextView(JSONObject json, String... propertyNames) throws JSONException {

            StringBuilder text = new StringBuilder();

            for(int i = 0; i < propertyNames.length; i++) {
                text.append(json.getString(propertyNames[i]));

                if(i < propertyNames.length - 1 )
                    text.append(" ");

            }

            return createTextView(text.toString());
        }

        private View createTextView(String text) {

            TextView textView = new TextView(LiveGamesActivity.this);

            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            textView.setTextColor(Color.BLACK);
            textView.setText(text);

            return textView;
        }

    private TableRow createTableRow(){
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        TableRow tr = new TableRow(LiveGamesActivity.this);
        tr.setLayoutParams(lp);
        tr.setPadding(0,15,0,15);
        return tr;
    }

    private class LiveGamesTask extends AsyncTask<String, Void, String> {

        final HttpClient client = new DefaultHttpClient();

        String data, content;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String result = "";

            try{
                HttpResponse httpResponse = client.execute(new HttpGet(params[0]));

                inputStream = httpResponse.getEntity().getContent();

                if(inputStream != null){
                    content = readStream(inputStream);
                }
                else{
                    content = "Oops, something went wrong";
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



            return content;
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

}
