package nyaa.bloonstd5cheats;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;
import com.stericson.RootTools.execution.CommandCapture;
import com.stericson.RootTools.execution.Shell;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import nyaa.bloonstd5cheats.cheats.*;

/**
 * Created by Oscar on 14/09/2014.
 */
public class CheatsHelper {

    private static final String LOG_TAG = CheatsHelper.class.getName();

    public static final String BLOONS_PACKAGE_NAME = "com.ninjakiwi.bloonstd5";

    private static List<Cheat> mCheats;

    private Context mContext;

    public CheatsHelper(Context context) {
        mContext = context;
    }

    public List<Cheat> getCheats() {
        if(mCheats == null) {
            mCheats = new ArrayList<Cheat>();

            mCheats.add(new JsonMergeCheat(mContext, "100 mil. Monkey money", R.raw.money));
            mCheats.add(new JsonMergeCheat(mContext, "100,000 Tokens", R.raw.tokens));
            mCheats.add(new JsonMergeCheat(mContext, "Rank 60", R.raw.rank));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock sandbox mode", R.raw.sandbox));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock fast track", R.raw.fasttrack));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock all towers", R.raw.towers));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock all lab items", R.raw.lab));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock all specialities", R.raw.specialities));
            mCheats.add(new JsonMergeCheat(mContext, "Unlock all agents", R.raw.agents));
        }

        return mCheats;
    }

    public boolean saveExists(String name) {
        return RootTools.exists(this.getSaveFilePath(name));
    }

    public boolean isInstalled() {
        return (this.getDataDirectory(BLOONS_PACKAGE_NAME) != null);
    }

    public String getDataDirectory(String packageName) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi.applicationInfo.dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public String getSaveFilePath(String name) {
        return this.getDataDirectory(BLOONS_PACKAGE_NAME) + "/files/" + name;
    }

    public boolean applyCheats(Cheat[] cheats) {
        killBloons();

        String dest = getDataDirectory(mContext.getPackageName()) + "/Profile.save";
        File file = new File(dest);
        if(file.exists()) {
            if(!file.delete()) {
                return false;
            }
        }

        try {
            new File(dest).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String src = getSaveFilePath("Profile.save");

        if(!RootTools.copyFile(src, dest, true, true)) {
            return false;
        }

        try {
            CommandCapture command = new CommandCapture(0, false, "chmod 666 " + dest);

            RootTools.getShell(true);
            Shell.runRootCommand(command);
            while(!command.isFinished()) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        SaveFile save = new SaveFile(dest);

        // try and read the file
        try {
            if(!save.read()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        for(int i = 0; i < cheats.length; i++) {
            Cheat cheat = cheats[i];

            if(!cheat.apply(save)) {
                return false;
            }
        }

        // write it back
        try {
            if(!save.write()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if(!RootTools.copyFile(dest, src, true, true)) {
            return false;
        }

        try {
            CommandCapture command = new CommandCapture(0, false, "chmod 666 " + src);

            RootTools.getShell(true);
            Shell.runRootCommand(command);
            while(!command.isFinished()) {
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void killBloons() {
        try {
            RootTools.killProcess(BLOONS_PACKAGE_NAME);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int doChecks() {
        if(!RootTools.isRootAvailable() || !RootTools.isAccessGiven()) {
            return R.string.error_no_root;
        }

        if(!isInstalled()) {
            return R.string.error_not_installed;
        }

        if(!saveExists("Profile.save")) {
            return R.string.error_no_save;
        }

        try {
            if(!RootTools.findBinary("chmod")) {
                return R.string.error_no_chmod;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return R.string.error_no_chmod;
        }

        return 0;
    }

    public boolean doBackup() {
        if(hasBackup()) { // file already exists, no need to backup
            return true;
        }

        String src = getSaveFilePath("Profile.save");
        String dest = getBackupFile();

        if(!RootTools.copyFile(src, dest, true, true)) {
            return false;
        }

        return true;
    }

    public boolean restoreBackup() {
        killBloons();

        if(!hasBackup()) {
            return false;
        }

        String src = getBackupFile();
        String dest = getSaveFilePath("Profile.save");

        if(!RootTools.copyFile(src, dest, true, true)) {
            return false;
        }

        return true;
    }

    public boolean hasBackup() {
        String dest = getBackupFile();
        File file = new File(dest);
        return file.exists();
    }

    public String getBackupFile() {
        return getDataDirectory(mContext.getPackageName()) + "/Profile.save.backup";
    }
}
