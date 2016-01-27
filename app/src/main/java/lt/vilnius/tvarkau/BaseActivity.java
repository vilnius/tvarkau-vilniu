package lt.vilnius.tvarkau;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import icepick.Icepick;

/**
 * Created by Karolis Vycius on 2016-01-15.
 */

/*
    This class also is responsible for keeping user google account signed in. It is very important, that all activities, that
    will be using google sign in, would extend this class.

 */
public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

}
