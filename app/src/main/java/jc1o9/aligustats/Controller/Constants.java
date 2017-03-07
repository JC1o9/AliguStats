package com.example.jose.aligustats.Controller;

/**
 * Constants class containing strings for the application
 * @author Jose
 */
public class Constants {
    public static final String URL_GET_10PLAYERS = "http://www.aligulac.com/api/v1/player/?current_rating__isnull=false&current_rating__decay__lt=4&order_by=-current_rating__rating&limit=10";
    public static final String URL_GET_10TEAMS = "http://www.aligulac.com/api/v1/team/?order_by=-scoreak&limit=10";
    public static final String URL_PREDICT_MATCH = "http://www.aligulac.com/api/v1/predictmatch/";
    public static final String URL_SEARCH_PLAYER = "http://www.aligulac.com/search/json/?q=";
    public static final String URL_GET_PLAYER_PROFILE = "http://www.aligulac.com/api/v1/player/";
    public static final String URL_CHECK_STREAMS = "https://api.twitch.tv/kraken/streams/medrybw";
    public static final String URL_ALIGULAC_WEBSITE = "aligulac.com";
    public static final String URL_GET_RANK = "http://www.aligulac.com";
    public static final String API_KEY = "&apikey=xqNO2nqL5vQU0ESGEoU7";
    public static final String PLAYER_ID = "PLAYER_ID";
    public static final String PLAYER1_NAME = "PLAYER1_NAME";
    public static final String PLAYER2_NAME = "PLAYER2_NAME";
    public static final String BEST_OF = "BEST_OF";
    public static final String ARRAY_LIST = "ARRAYLIST";
    public static final String NO_CONNECTION = "NO_CONNECTION";
    public static final String STREAM_RESULT = "STREAM_RESULT";
    public static final String STREAM_RESULT_VAL = "STREAM_RESULT_VAL";
}
