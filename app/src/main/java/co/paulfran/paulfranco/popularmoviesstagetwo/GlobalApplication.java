package co.paulfran.paulfranco.popularmoviesstagetwo;

import android.app.Application;

import com.orhanobut.logger.AndroidLogAdapter;

public class GlobalApplication extends Application {

    public GlobalApplication() {
        com.orhanobut.logger.Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.DEBUG;
            }
        });
    }

}