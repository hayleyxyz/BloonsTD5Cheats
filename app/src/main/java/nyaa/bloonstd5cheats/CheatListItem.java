package nyaa.bloonstd5cheats;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import nyaa.bloonstd5cheats.cheats.Cheat;

/**
 * Created by Oscar on 13/09/2014.
 */
public class CheatListItem extends LinearLayout {

    private Cheat mCheat;

    public CheatListItem(Context context) {
        super(context);
    }

    public CheatListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheatListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCheat(Cheat cheat) {
        mCheat = cheat;

        ((TextView)findViewById(R.id.cheatTitle)).setText(cheat.getName());
    }

    public Cheat getCheat() {
        return mCheat;
    }
}
