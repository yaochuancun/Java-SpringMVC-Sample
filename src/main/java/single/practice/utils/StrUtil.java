package single.practice.utils;

import java.util.UUID;

public abstract class StrUtil {
    public static String generatorUUID() {
        String uuid = UUID.randomUUID().toString();
        StringBuilder s = new StringBuilder();
        return s.append(uuid.substring(0, 8)).append(uuid.substring(9, 13)).append(uuid.substring(14, 18)).append(uuid.substring(19, 23)).append(uuid.substring(24)).toString();
    }

    /**
     * 去掉文本的换行符，使在一行上面.
     */
    public static String toOneLine(String input) {

        if (input == null) {
            return null;
        }
        if (input.indexOf("\r\n") >= 0) {
            return input.replace("\r\n", "\\r\\n");
        } else {
            return input.replace("\n", "\\n");
        }

    }

    public static String hideIPinURL(String url){
        if(null !=url &&(url.startsWith("https")||(url.startsWith("http")))){
            return url.replaceFirst("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.","*.*.*.");
        }else{
            return url;
        }
    }

    /**
     * <p>
     * Test if a string is empty.
     *
     * @return
     */
    public static boolean isEmpty(String s) {

        return s == null || "".equals(s.trim());
    }


}
