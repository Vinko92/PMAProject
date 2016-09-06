package pma.vinko.legendtracker.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.SupportActionModeWrapper;
import android.view.View;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import pma.vinko.legendtracker.ChampionDetails;
import pma.vinko.legendtracker.R;
import pma.vinko.legendtracker.asynctasks.GetData;
import pma.vinko.legendtracker.asynctasks.GetImage;
import pma.vinko.legendtracker.asynctasks.GetSummonerId;
import pma.vinko.legendtracker.helpers.UrlBuilder;

public class LiveGamesActivity extends AppCompatActivity {

    private Spinner spinner;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_games);

        //Init layout
        setSpinner();
        setButton();
    }

    private void setSpinner(){
        spinner = (Spinner) findViewById(R.id.drpRegions);
        String[] regions = new String[]{ "EUNE","EUW","NA","BR","TR","JP","KR","LAN","LAS","OCE","PBE","RU","TR" };

        ArrayAdapter<String> arrayAdapter =  new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, regions);
        spinner.setAdapter(arrayAdapter);
    }

    private void setButton() {
        button = (Button) findViewById(R.id.btnFetch);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText userInput = (EditText) findViewById(R.id.userInput);
                String url = UrlBuilder.BuildUrl("https://eune.api.pvp.net/api/lol/", spinner.getSelectedItem().toString(), "/v1.4/summoner/by-name/",
                        userInput.getText().toString(), "?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA");


                try {
                    JSONObject jsonData = new JSONObject(new GetData().execute(url).get());
                    String summonerId = jsonData.getJSONObject(jsonData.keys().next()).getString("id");
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
            ProgressDialog progressDialog = new ProgressDialog(LiveGamesActivity.this);
            progressDialog.setTitle("Please wait...");
            progressDialog.show();

            try {

                JSONObject json = new JSONObject(data);
                StringBuilder gameDetails = new StringBuilder();

                TableLayout tl = (TableLayout) findViewById(R.id.gameDetails);

                parseBannedChampions(json.getJSONArray("bannedChampions"),tl);
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

        private void parseBannedChampions(JSONArray json, TableLayout tl) throws JSONException {

            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

            for(int i = 0; i < json.length(); i++) {
                TableRow tr = new TableRow(LiveGamesActivity.this);
                tr.setLayoutParams(lp);


                JSONObject jsonObject = json.getJSONObject(i);
                String championId = jsonObject.getString("championId");

                try {
                    tr.addView(getImage(championId));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ImageView imageView = new ImageView(LiveGamesActivity.this);
                imageView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                new GetImage(imageView).execute("https://global.api.pvp.net/api/lol/static-data/eune/v1.2/champion/"+championId+"?champData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA");
                tr.addView(imageView);

                //tr.addView(getTextView(jsonObject, "championId"));
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }


        }


        private View getImage(String championId) throws ExecutionException, InterruptedException, JSONException {
            ImageView image = new ImageView(LiveGamesActivity.this);
            image.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            JSONObject jsonObject = new JSONObject(new GetData().execute("https://global.api.pvp.net/api/lol/static-data/eune/v1.2/champion/"+championId+"?champData=all&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA").get());
            String imageName = jsonObject.getJSONObject("image").getString("full");

            StringBuilder text = new StringBuilder();



            return createTextView(imageName);
            //new GetImage(image)
                 //   .execute("http://ddragon.leagueoflegends.com/cdn/6.18.1/img/champion/Sona.png");

            //return image;
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
