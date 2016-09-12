package pma.vinko.legendtracker.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pma.vinko.legendtracker.R;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = "RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA";
    private static final String TEST = "https://na.api.pvp.net/api/lol/na/v1.4/summoner/by-name/RiotSchmick?api_key=" + API_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button data = (Button) findViewById(R.id.data);

        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,StaticDataActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button liveGames = (Button) findViewById(R.id.btnLiveGames);

        liveGames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LiveGamesActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button settings = (Button) findViewById(R.id.btnSettings);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivity(intent);
                finish();
            }
        });

        Button players = (Button) findViewById(R.id.btnPlayers);

        players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlayersActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button quit = (Button) findViewById(R.id.btnQuit);

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    private class LiveGamesTask extends AsyncTask<String, Void, Void>{


        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

}

