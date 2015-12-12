package nyaa.bloonstd5cheats;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import nyaa.bloonstd5cheats.cheats.*;


public class MainActity extends Activity implements AdapterView.OnItemClickListener, CheatTask.OnCheatTaskCompleted {

    private static final String LOG_TAG = MainActity.class.getName();

    private CheatsHelper mCheatsHelper;
    private List<Cheat> mCheatQueue;
    private CheatTask mCheatTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_actity);

        mCheatQueue = new ArrayList<Cheat>();

        mCheatsHelper = new CheatsHelper(this);

        int result = mCheatsHelper.doChecks();
        if(result != 0) {
            String error = getString(result);
            Log.e(LOG_TAG, "doChecks() failed: " + error);

            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
            intent.putExtra(ErrorActivity.EXTRA_ERROR_RES, result);
            startActivity(intent);

            finish();
            return;
        }

        mCheatsHelper.doBackup();

        ListView cheatsList = (ListView)findViewById(R.id.cheatsList);

        List<Cheat> cheats = mCheatsHelper.getCheats();
        CheatsListAdapter listAdapter = new CheatsListAdapter(this, cheats);
        cheatsList.setAdapter(listAdapter);
        cheatsList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        List<Cheat> cheats = mCheatsHelper.getCheats();

        if(i < cheats.size()) {
            Cheat cheat = cheats.get(i);
            mCheatQueue.add(cheat);

            processCheatQueue();
            CheatTask task = new CheatTask(this, mCheatsHelper);

            task.execute(cheat);

            ((CheatListItem) view).setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        }
        else {
            final Context context = this;

            new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.backup_title)
                .setMessage(R.string.backup_confirm)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(mCheatsHelper.restoreBackup()) {
                            Toast.makeText(context, R.string.backup_success, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(context, R.string.backup_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
        }
    }

    private void processCheatQueue() {
        if(mCheatTask == null && mCheatQueue.size() > 0) {
            mCheatTask = new CheatTask(this, mCheatsHelper);
            mCheatTask.setOnComplete(this);

            mCheatTask.execute(mCheatQueue.toArray(new Cheat[mCheatQueue.size()]));
            mCheatQueue.clear();
        }
    }

    @Override
    public void onComplete(Boolean result) {
        if(!result) {
            Toast.makeText(this, R.string.cheats_fail, Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, R.string.cheats_success, Toast.LENGTH_SHORT).show();
        }

        Cheat[] cheats = mCheatTask.getCheats();

        for(int i = 0; i < cheats.length; i++) {
            Log.d(LOG_TAG, "Applied cheat: " + cheats[i].getName());
        }

        mCheatTask = null;
        processCheatQueue();
    }
}
