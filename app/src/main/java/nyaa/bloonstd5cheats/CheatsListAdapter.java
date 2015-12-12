package nyaa.bloonstd5cheats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import nyaa.bloonstd5cheats.cheats.Cheat;

/**
 * Created by Oscar on 13/09/2014.
 */
public class CheatsListAdapter extends BaseAdapter {

    private List<Cheat> mCheats;
    private LayoutInflater mInflater;

    public CheatsListAdapter(Context context, List<Cheat> galleries) {
        mCheats = galleries;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mCheats.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if(i < mCheats.size()) {
            return mCheats.get(i);
        }
        else {
            return "backup";
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(i < mCheats.size()) {
            CheatListItem listItem;
            Cheat cheat = mCheats.get(i);

            if (view == null || !(view instanceof CheatListItem)) {
                listItem = (CheatListItem)mInflater.inflate(R.layout.cheat_list_item, null);
            } else {
                listItem = (CheatListItem)view;
            }

            listItem.setCheat(cheat);
            return listItem;
        }
        else {
            View listItem = mInflater.inflate(R.layout.backup_list_item, null);
            return listItem;
        }
    }
}
