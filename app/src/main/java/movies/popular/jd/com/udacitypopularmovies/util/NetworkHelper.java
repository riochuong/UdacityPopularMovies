package movies.popular.jd.com.udacitypopularmovies.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by chuondao on 1/21/17.
 */

public class NetworkHelper {

    /**
     * Check if we are connecting to network
     * need to be called on separate thread
     *
     * @param ctx
     * @return true
     */
    public static boolean isConnectToInternet(Context ctx) {
        ConnectivityManager conMan =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        return (netInfo != null) && (netInfo.isConnected());
    }

}
