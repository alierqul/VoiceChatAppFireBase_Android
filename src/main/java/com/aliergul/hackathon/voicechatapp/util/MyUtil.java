package com.aliergul.hackathon.voicechatapp.util;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtil {
    public static final String USER_UID="uid";
    public static final String FULL_NAME="fullName";
    public static final String COLUMN_USERS="Users";
    public static final String COLUMN_MESSAGES="messages";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Email Adresi Kontrol etme.
     * @param emailStr
     * @return
     */
    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    public static String dateToString(long l){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE, MMMM d, yyyy");
       return dateFormatter.format(l);
    }
}
