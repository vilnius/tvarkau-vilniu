package lt.vilnius.tvarkau;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class TvarkauApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
