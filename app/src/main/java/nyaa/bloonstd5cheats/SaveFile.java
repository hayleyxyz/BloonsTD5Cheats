package nyaa.bloonstd5cheats;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by Oscar on 13/09/2014.
 */
public class SaveFile {

    private static final String LOG_TAG = SaveFile.class.getName();
    protected static final String SAVE_MAGIC = "DGDATA";
    protected static final int HEADER_LENGTH = (SAVE_MAGIC.length() + 8); // magic + crc

    protected String mFilePath;
    protected JSONObject mJson;

    public SaveFile(String filePath) {
        mFilePath = filePath;
    }

    public JSONObject getJson() {
        return mJson;
    }

    public boolean read() throws Exception {
        byte[] buffer = readFile(mFilePath);

        if(buffer.length <= HEADER_LENGTH) {
            Log.e(LOG_TAG, "Invalid save file length: " + buffer.length);
            return false;
        }

        // extract magic and crc
        String magic = new String(Arrays.copyOfRange(buffer, 0, SAVE_MAGIC.length()), "UTF-8");
        long crc = Long.parseLong(new String(Arrays.copyOfRange(buffer, SAVE_MAGIC.length(), (SAVE_MAGIC.length() + 8)), "UTF-8"), 16);

        // verify magic
        if(magic.compareTo(SAVE_MAGIC) != 0) {
            Log.e(LOG_TAG, "Invalid save magic");
            return false;
        }

        Log.d(LOG_TAG, "CRC: " + Long.toString(crc, 16));

        // decrypt the remaining data
        byte[] body = Arrays.copyOfRange(buffer, HEADER_LENGTH, buffer.length);
        Crypt.decrypt(body, 0, body.length);

        // calc and verify crc
        long realCrc = Crypt.crc(body, 0, body.length);
        if(realCrc != crc) {
            Log.e(LOG_TAG, "CRC mismatch: " + Long.toString(realCrc, 16));
            return false;
        }

        // load resulting JSON
        String jsonString = new String(body, "UTF-8");
        mJson = new JSONObject(jsonString);

        return true;
    }

    public boolean write() throws Exception{
        // convert JSON
        String jsonString = mJson.toString(4);
        byte[] jsonBuffer = jsonString.getBytes("UTF-8");

        // target buffer
        byte[] buffer = new byte[HEADER_LENGTH + jsonBuffer.length];

        // copy magic
        byte[] magic = SAVE_MAGIC.getBytes("UTF-8");
        System.arraycopy(magic, 0, buffer, 0, magic.length);

        // crc JSON and copy to target
        long crc = Crypt.crc(jsonBuffer, 0, jsonBuffer.length);
        String crcString = String.format("%8s", Long.toString(crc, 16)).replace(' ', '0');
        byte[] crcBuffer = crcString.getBytes("UTF-8");
        System.arraycopy(crcBuffer, 0, buffer, magic.length, crcBuffer.length);

        // encrypt json and copy
        Crypt.encrypt(jsonBuffer, 0, jsonBuffer.length);
        System.arraycopy(jsonBuffer, 0, buffer, HEADER_LENGTH, jsonBuffer.length);

        // write target buffer to files
        writeFile(mFilePath, buffer);

        return true;
    }

    protected byte[] readFile(String path) throws Exception {
        int length = (int)new File(path).length();

        FileInputStream fis = new FileInputStream(path);
        DataInputStream dis = new DataInputStream(fis);

        byte[] buffer = new byte[length];
        dis.readFully(buffer);
        dis.close();

        return buffer;
    }

    protected void writeFile(String path, byte[] buffer) throws Exception {
        File file = new File(path);
        if(file.exists()) {
            file.delete();
        }

        FileOutputStream fis = new FileOutputStream(path);
        DataOutputStream dis = new DataOutputStream(fis);

        dis.write(buffer, 0, buffer.length);
        dis.close();
    }

}
