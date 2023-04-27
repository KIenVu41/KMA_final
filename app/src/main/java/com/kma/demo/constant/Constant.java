package com.kma.demo.constant;

public class Constant {

    public static final String GENERIC_ERROR = "General error, please try again later";
    public static final String DOWNLOAD_DIR = "/storage/emulated/0/Download/";

    // Firebase url
    public static final String FIREBASE_URL = "https://musicbasic-251ca-default-rtdb.firebaseio.com";
    //public static final String BASE_URL = "http://10.0.2.2:3000/api/v1/";
    public static final String BASE_URL = "http://192.168.0.100:3000/api/v1/";
    // Max count
    public static final int MAX_COUNT_BANNER = 5;
    public static final int MAX_COUNT_POPULAR = 4;
    public static final int MAX_COUNT_LATEST = 4;

    // Music actions
    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int NEXT = 2;
    public static final int PREVIOUS = 3;
    public static final int RESUME = 4;
    public static final int CANNEL_NOTIFICATION = 5;
    public static final String MUSIC_ACTION = "musicAction";
    public static final String SONG_POSITION = "songPosition";
    public static final String CHANGE_LISTENER = "change_listener";
    public static final String LIBRARY_ACTION = "libraryAction";
    public static Boolean IS_LIBRARY = false;
}
