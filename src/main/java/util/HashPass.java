package util;

import org.mindrot.jbcrypt.BCrypt;

public class HashPass {

    public static String hashPassword(String planPassword) {
        return BCrypt.hashpw(planPassword, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String planPassword, String hashedPassword) {
        return BCrypt.checkpw(planPassword, hashedPassword);
    }
}
