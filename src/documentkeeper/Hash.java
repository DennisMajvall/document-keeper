package documentkeeper;

import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Hash {
    private static final String strKey ="Blowfish";

    public static long checksum(String input){ return checksum(input.getBytes()); }
    public static long checksum(byte[] bytes){
        Checksum checksum = new CRC32();

        // update the current checksum with the specified array of bytes
        checksum.update(bytes, 0, bytes.length);

        return checksum.getValue();
    }

    public static String encrypt(String text) {
        try {
            SecretKeySpec key = new SecretKeySpec(strKey.getBytes("UTF8"), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.getEncoder().encodeToString(cipher.doFinal(text.getBytes("UTF8")));
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
        return "";
    }

    public static String decrypt(String text) {
        try {
            byte[] encryptedData = Base64.getDecoder().decode(text);
            SecretKeySpec key = new SecretKeySpec(strKey.getBytes("UTF8"), "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(encryptedData);
            return new String(decrypted);
        } catch (Exception ex) { Logger.getGlobal().log(Level.SEVERE, null, ex); }
        return "";
    }

}
