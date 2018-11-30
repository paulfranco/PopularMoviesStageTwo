package co.paulfran.paulfranco.popularmoviesstagetwo.utils;

import android.content.Context;
import android.net.ConnectivityManager;

import java.util.Objects;

public class Misc {

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return Objects.requireNonNull(cm).getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

    }

}