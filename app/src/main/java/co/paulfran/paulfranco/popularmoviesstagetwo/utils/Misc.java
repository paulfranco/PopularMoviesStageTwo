package co.paulfran.paulfranco.popularmoviesstagetwo.utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class Misc {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // May need to remove this assertion in the future
        assert cm != null;
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}