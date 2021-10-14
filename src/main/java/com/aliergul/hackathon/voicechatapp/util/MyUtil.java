package com.aliergul.hackathon.voicechatapp.util;

import android.util.Log;

import com.aliergul.hackathon.voicechatapp.model.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyUtil {
    private static final String TAG ="MyUtil" ;
    public static final String USER_UID="uid";
    public static final String FULL_NAME="fullName";
    public static final String COLUMN_USERS="Users";
    public static final String COLUMN_MESSAGES="messages";
    public static final String IS_ONLINE="online";
    public static final String COLUMN_VOICES="voices";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static Locale locale=new Locale("tr","Turkey");

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
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
       return sdf.format(l);
    }

    public static String getTimestampSecond(Post post){
        int minute=getTimestampDifference(post);
        Log.w(TAG,"minute ="+minute);
        if(minute>=0 && minute<= 1){
            return "Şimdi";
        }else if(minute>1 && minute< 60){
            return minute +" dk önce";
        }else if(minute>=60){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
            Date date=new Date(post.getDate());
            return sdf.format(date);
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));
            Date date=new Date(post.getDate());
            return  sdf.format(date);
        }

    }

    private static int getTimestampDifference(Post post){
        //Simple Date Format Tanımlama

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",locale);
        sdf.setTimeZone(TimeZone.getTimeZone("Turkey"));

        int difference = 0;

        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        timestamp = new Date(post.getDate());                                       //Dakika hesapladı.
        difference = Math.round((today.getTime() - post.getDate()) / 1000 / 60 );

        return difference;
    }
}
