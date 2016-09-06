package pma.vinko.legendtracker.helpers;

/**
 * Created by Vinko on 4.9.2016..
 */
public class UrlBuilder {

    public static String BuildUrl(String... parameters){
        StringBuilder url = new StringBuilder();

        for(String parameter : parameters){
            url.append(parameter);
        }

        return url.toString();
    }
}
