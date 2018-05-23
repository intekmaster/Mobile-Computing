package com.example.sushantpaygude.finalproject.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sushantpaygude on 4/19/18.
 */

public class Utilities {

    //YELP API IDS AND URLS
    public static String YELPAuthorization = "Authorization";

    public static String YELP_CLIENTID = "RgcCYFc4FYIUY_bGRgmcTQ";
    public static String YELP_API_KEY = "Bearer PasfAv9a_1h1ig_o6imYqFnyJJ1oiSiXkdPRx3sdmXUQrA55-5NAf2PyZIL_zy3Wml8SseXG1NpUS3vdA9Ev9q4dUgwsyecHBXwwhlaWE8atMIgMVK6BQynOuCbZWnYx";


    public static String YELP_SEARCH_BASE_STRING = "https://api.yelp.com/v3/businesses/search?";

    public static String YELP_GET_RESTAURANTS_BY_LOCATION = YELP_SEARCH_BASE_STRING + "latitude=%s&longitude=%s&radius=10000";


    public static String GOOGLE_PLACES_API = "";
    public static String RESTAURANTS = "Restaurants";
    public static int RC_SIGN_IN = 999;

    public static Map<String, String> getYELPTokenParams(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Map<String, String> params = new HashMap<>();
        params.put(YELPAuthorization, YELP_API_KEY);
        return params;
    }


    public static String GOOGLESIGNINACCOUNT = "GOOGLESIGNINACCOUNT";
    public static String SERVERSIGNINACCOUNT = "SERVERSIGNINACCOUNT";


    //TICKETMASTERS API IDS AND URLS
    public static String TicketMasterAuthorization = "Authorization";
    public static String TICKETMASTER_API_KEY = "xci6BKuaudQC0tMXRUZnvFSIF6trOVfd";
    public static String TICKETMASTER_SEARCH_BASE_STRING = "https://app.ticketmaster.com/discovery/v2/events.json?";
    public static String TICKETMASTER_GET_EVENTS_BY_LOCATION = TICKETMASTER_SEARCH_BASE_STRING + "geoPoint=%s&radius=%s&apikey=%s&page=%s";
    public static String EVENTS = "Events";

    public static Map<String, String> getTicketMasterTokenParams(Context context) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        Map<String, String> params = new HashMap<>();
        params.put(TicketMasterAuthorization, TICKETMASTER_API_KEY);
        return params;
    }

    //"https://app.ticketmaster.com/discovery/v2/events.json?geoPoint=dqcrq&radius=10&apikey=xci6BKuaudQC0tMXRUZnvFSIF6trOVfd"


    //MapBox API Key
    public static String MAPBOX_API_KEY = "pk.eyJ1IjoicGFua2FqdGVrd2FuaSIsImEiOiJjamd5Yml2dmszNHd3MnltbGQ4b2hqZmo5In0.LxtbuVhmkaj1QI8RaGiVaQ";

    //GOOGLE PLACES API IDS AND URLS
    public static String ATMS = "ATMS/Banks";
    public static String GAS_STATION = "Gas Station";
    public static String GOOGLEPLACES_API_KEY = "AIzaSyBer4uEGhaM6RK9eYMz4OoptFg3cxU9bGk";
    public static String GOOGLEPLACES_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static String GET_ATMS_BY_LOCATION = GOOGLEPLACES_BASE_URL + "location=%s,%s&radius=1500&type=atm&key=%s&pagetoken=%s";
    public static String GET_GAS_STATION_BY_LOCATION = GOOGLEPLACES_BASE_URL + "location=%s,%s&radius=1500&type=gas_station&key=%s&pagetoken=%s";


    public static String TO_DO_LIST_STRING = "TO_DO_LIST_STRING";


    public static String REGISTRATION_URL = "http://745d9c0f.ngrok.io/hug/hug.php";


}
