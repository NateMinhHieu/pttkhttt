package garaman.model;
import java.util.*;
import java.time.*;
import java.math.BigDecimal;

public class MemberAccount9 {
    public long id;
    public String username;
    public String credential;
    public String role;
    public String accountStatus;

    public static boolean verifyPassword(String password, String salt, String credential) throws java.security.GeneralSecurityException {
        var spec = new javax.crypto.spec.PBEKeySpec(password.toCharArray(), HexFormat.of().parseHex(salt), 100000, 256);
        try {
            byte[] key = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
            return java.security.MessageDigest.isEqual(key, HexFormat.of().parseHex(credential));
        } finally {
            spec.clearPassword();
        }
    }
}
