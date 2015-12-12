package nyaa.bloonstd5cheats;

import android.util.Log;

import java.math.BigInteger;

/**
 * Created by Oscar on 13/09/2014.
 */
public class Crypt {

    public static void decrypt(byte[] buffer, int start, int length) {
        for(int i = 0; i < length; i++) {
            buffer[start + i] -= (i % 6) + 0x15;
        }
    }

    public static void encrypt(byte[] buffer, int start, int length) {
        for(int i = 0; i < length; i++) {
            buffer[start + i] += (i % 6) + 0x15;
        }
    }

    public static long crc(byte[] buffer, int start, int length) {
        int crc = 0;

        for(int i = 0; i < length; i++) {
            crc ^= buffer[start + i];
            int edx = (crc >>> 8);
            int eax = crcUpdate(crc & 0xff);
            crc = eax ^ edx;
        }

        return (crc & 0x00000000ffffffffL); // extend to long and clear upper
    }

    protected static int crcUpdate(int crc) {
        for(int i = 0; i < 8; i++) {
            if((crc & 1) == 1) {
                crc = (crc >> 1);
                crc ^= 0xEDB88320;
            }
            else {
                crc = (crc >> 1);
            }
        }

        return crc;
    }

}
