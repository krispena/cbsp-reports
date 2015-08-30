package gov.texas.tpwd.mobileranger;

import android.app.Application;
import android.content.Context;

/**
 * Created by kris on 8/30/15.
 */
public class MobileRangerApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MobileRangerApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MobileRangerApplication.context;
    }
}
