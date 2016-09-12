package pma.vinko.legendtracker.helpers;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by Vinko on 12.9.2016..
 */
public class PlayerHelper {

    private static String leagueStatsUrl = "https://eune.api.pvp.net/api/lol/eune/v2.5/league/by-summoner/%s?api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA";
    private static String champStatsUrl = "https://eune.api.pvp.net/api/lol/eune/v1.3/stats/by-summoner/%s/ranked?season=SEASON2016&api_key=RGAPI-CBA5D9B2-7ED7-42FB-A7F0-D312B30965EA";

    public static String getLeagueStats(String playerId){
        JSONObject json = null;
        String retVal = "";
        int retry = 3;
        do {
            try {
                String url = String.format(leagueStatsUrl, playerId);
                InputStream firstStream = new java.net.URL(url).openStream();
                json = new JSONObject(readStream(firstStream));
                retVal= "   " + json.getJSONArray(json.keys().next()).getJSONObject(0).getString("tier") + " "+
                        json.getJSONArray(json.keys().next()).getJSONObject(0).getJSONArray("entries").getJSONObject(0).getString("division");
                retry = -1;
            } catch (Exception e) {
                retry--;
                if(retry == 1){
                    e.printStackTrace();
                }
            }
        }while(retry > 1);

       return retVal;
    }

    public static String getKDA(String playerId, String championId){
        JSONObject json = null;
        String kda = "";
        try {
            InputStream firstStream = new java.net.URL(String.format(champStatsUrl, playerId)).openStream();
            json = new JSONObject(readStream(firstStream));



        JSONArray champions = json.getJSONArray("champions");
        for(int i = 0; i < champions.length(); i++){
            JSONObject champion = champions.getJSONObject(i);

            if(champion.getString("id").equals(championId)){

                JSONObject stats = champion.getJSONObject("stats");
                kda = stats.getString("totalSessionsPlayed") + "   " + new DecimalFormat("##.##").format((((double)(stats.getDouble("totalChampionKills") + stats.getDouble("totalAssists")))/((double)stats.getInt("totalDeathsPerSession"))));
            }
        }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return kda;
    }

    @NonNull
    private static String readStream(InputStream stream) throws IOException {
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
