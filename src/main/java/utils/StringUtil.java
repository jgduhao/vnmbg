package utils;

import java.util.Random;

public class StringUtil {

    public static String getMessage(String template,String...args){
        for(int i=0; i<args.length; i++){
            template = template.replaceAll("\\{"+i+"\\}", args[i]);
        }
        return template;
    }

    public static String randomPosterSign(){
        String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<7; i++){
            int charIndex = random.nextInt(source.length());
            builder.append(source.charAt(charIndex));
        }
        return builder.toString();
    }

    public static String leftPaddingZeroToEight(Integer num){
        return String.format("%08d",num);
    }


}
