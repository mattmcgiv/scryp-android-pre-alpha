package io.scryp.scryp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Created by Matt on 8/24/2017.
 */

public class QRUtilities {
    final static String TAG = "Scryp";
    public static boolean isQRFormattedCorrectly(String qrContent) {
        try {
            JSONObject qrJSON = getJSONObj(qrContent);
            qrJSON.get("deal");
            qrJSON.get("recipient");
        } catch (JSONException je) {
            Log.v(TAG, "JSONException detected in ConfirmTransactionActivity");
            return false;
        }
        return true;
    }


    public static JSONObject getJSONObj (String json) throws JSONException {
        return (JSONObject) new JSONTokener(json).nextValue();
    }
}
