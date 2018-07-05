package single.practice.utils.string;

import org.apache.commons.lang.StringUtils;

public class EncodeForLogUtil {
    public static String handleLogForging(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        return str.replace("\r", "_").replace("\n", "_");
    }

    public static String handleLogForging(Object ob) {
        if (ob == null) {
            return "";
        }
        return handleLogForging(ob.toString());
    }

}
