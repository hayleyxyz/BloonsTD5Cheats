package nyaa.bloonstd5cheats.cheats;

import android.content.Context;

import nyaa.bloonstd5cheats.SaveFile;

/**
 * Created by Oscar on 14/09/2014.
 */
public class JsonMergeCheat extends Cheat {

    protected String mName;
    protected int mResId;

    public JsonMergeCheat(Context context, String name, int resId) {
        super(context);

        mName = name;
        mResId = resId;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean apply(SaveFile saveFile) {
        return applyJson(saveFile, mResId);
    }
}
