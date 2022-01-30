package utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.xml.stream.events.Characters;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

public class PasswordEncryption {
    private static final Random random = new SecureRandom();
    private static final String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int iterations = 1000;
    private static final int keyLength = 256;

    /**
     * method that generates a Salt Value of size 'length'
     * @param length - int
     * @return - String
     */
    public static String getSaltValue(int length){
       StringBuilder builder = new StringBuilder(length);
       for(int i = 0; i < length; i++){
           char randomCharacter = characters.charAt(random.nextInt(characters.length()));
           builder.append(randomCharacter);
       }
       return builder.toString();
    }

    /**
     * method that hashes the given password using a Salt Value(parameter), returning a byte Array
     * @param password - char[]
     * @param salt - byte[]
     * @return - byte[]
     */
    public static byte[] hash(char[] password, byte[] salt)
    {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
        Arrays.fill(password, Character.MIN_VALUE);
        try
        {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e)
        {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        }
        finally
        {
            spec.clearPassword();
        }
    }

    /**
     * method that encrypts a given password, using a Salt Value
     * @param password - String
     * @param salt - String
     * @return
     */
    public static String generateEncryptedPassword(String password, String salt){
        byte[] hashedPassword = hash(password.toCharArray(), salt.getBytes());

        String encryptedPassword = Base64.getEncoder().encodeToString(hashedPassword);

        return encryptedPassword;
    }
}
