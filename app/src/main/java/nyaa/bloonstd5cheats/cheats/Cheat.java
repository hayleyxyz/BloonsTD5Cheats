package nyaa.bloonstd5cheats.cheats;

import android.content.Context;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;

import nyaa.bloonstd5cheats.SaveFile;
import nyaa.bloonstd5cheats.Utils;

/**
 * Created by Oscar on 13/09/2014.
 */
public abstract class Cheat {

    protected Context mContext;

    public Cheat(Context context) {
        mContext = context;
    }

    public abstract String getName();
    public abstract boolean apply(SaveFile saveFile);

    protected boolean applyJson(SaveFile saveFile, int resId) {
        // load json from raw resources
        InputStream resStream = mContext.getResources().openRawResource(resId);
        String mergeJsonString = Utils.streamToString(resStream);

        JSONObject mergeJson;

        try {
            mergeJson = new JSONObject(mergeJsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        // save file json
        JSONObject saveJson = saveFile.getJson();

        // merge json
        try {
            mergeJson(mergeJson, saveJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private static void mergeJson(JSONObject source, JSONObject target) throws JSONException {
        Iterator<?> keys = source.keys();

        while( keys.hasNext() ){
            String key = (String)keys.next();
            Object srcItem = source.get(key);

            if(!target.has(key)) {
                target.put(key, srcItem);
            }
            else {
                if(srcItem instanceof JSONObject) {
                    mergeJson((JSONObject) srcItem, target.getJSONObject(key));
                }
                else {
                    target.put(key, srcItem);
                }
            }
        }
    }

}
