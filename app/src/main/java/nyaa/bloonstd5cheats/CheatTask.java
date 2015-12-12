package nyaa.bloonstd5cheats;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import nyaa.bloonstd5cheats.cheats.Cheat;

/**
 * Created by Oscar on 15/09/2014.
 */
public class CheatTask extends AsyncTask<Cheat, Object, Boolean> {

    private Context mContext;
    private CheatsHelper mHelper;
    private OnCheatTaskCompleted mOnComplete;
    private Cheat[] mCheats;

    public CheatTask(Context context, CheatsHelper helper) {
        mContext = context;
        mHelper = helper;
    }

    public void setOnComplete(OnCheatTaskCompleted onComplete) {
        mOnComplete = onComplete;
    }

    public Cheat[] getCheats() {
        return mCheats;
    }

    @Override
    protected Boolean doInBackground(Cheat[] objects) {
        mCheats = objects;
        try {
            if(!mHelper.applyCheats(objects)) {
                return false;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(mOnComplete != null) {
            mOnComplete.onComplete(result);
        }
    }

    public interface OnCheatTaskCompleted {

        public void onComplete(Boolean result);

    }
}
