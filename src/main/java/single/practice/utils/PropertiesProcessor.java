package single.practice.utils;

import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.StringUtils;
import org.wcc.crypt.Crypter;
import org.wcc.crypt.CrypterFactory;
import org.wcc.framework.AppRuntimeException;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * <一句话功能简述> <功能详细描述>
 *
 * @author yWX243867
 * @version [版本号, 2015年4月20日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public  class PropertiesProcessor extends DefaultPropertiesPersister {

	private static final String REDIS_PW = "redis.pwd";

	private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesProcessor.class);
	private final Crypter crypter = CrypterFactory.getCrypter(CrypterFactory.AES_CBC);

	private static List<String> DEC_VALUE_LIST = new ArrayList<String>();
	static {
//		DEC_VALUE_LIST.add(REDIS_PW);
	}


	public void load(Properties props, Reader reader){
		  LineIterator lineIterator = new LineIterator(reader);
		    String line = null;
		    String nextLine = null;
		    char firstChar = '\000';
		    while (lineIterator.hasNext()) {
		      line = lineIterator.nextLine();
		      if (line == null) {
		        return;
		      }
		      line = StringUtils.trimLeadingWhitespace(line);
		      if (line.length() > 0) {
		        firstChar = line.charAt(0);
		        if ((firstChar != '#') && (firstChar != '!')) {
		          while (endsWithContinuationMarker(line)) {
		            nextLine = lineIterator.nextLine();
		            line = line.substring(0, line.length() - 1);
		            if (nextLine != null) {
		              StringBuffer lineBuffer = new StringBuffer(line);
		              line = lineBuffer.append(StringUtils.trimLeadingWhitespace(nextLine)).toString();
		            }
		          }
		          int separatorIndex = line.indexOf('=');
		          if (separatorIndex == -1) {
		            separatorIndex = line.indexOf(':');
		          }
		          String key = separatorIndex != -1 ? line.substring(0, separatorIndex) : line;

		          String value = separatorIndex != -1 ? line.substring(separatorIndex + 1) : "";
		          key = StringUtils.trimTrailingWhitespace(key);
		          value = StringUtils.trimLeadingWhitespace(value);
		          if (DEC_VALUE_LIST.contains(key)) {
		            value = decodeValue(value);
		          }
		          if(props!=null && key!=null && value !=null)
		        	  props.put(unescape(key), unescape(value));
		        }
		      }
	}
	}

	protected boolean endsWithContinuationMarker(String line) {
		boolean evenSlashCount = true;
		int index = line.length() - 1;
		while (index >= 0 && line.charAt(index) == '\\') {
			evenSlashCount = !evenSlashCount;
			index--;
		}
		return !evenSlashCount;
	}

	protected String unescape(String str) {
		StringBuffer outBuffer = new StringBuffer(str.length());
		for (int index = 0; index < str.length();) {
			char c = str.charAt(index++);
			if (c == '\\') {
				c = str.charAt(index++);
				if (c == 't') {
					c = '\t';
				} else if (c == 'r') {
					c = '\r';
				} else if (c == 'n') {
					c = '\n';
				} else if (c == 'f') {
					c = '\f';
				}
			}
			outBuffer.append(c);
		}
		return outBuffer.toString();
	}

	private String decodeValue(String value) {
		String value1 = "";
		try {
			value1 = crypter.decrypt(value);
		} catch (IllegalArgumentException e) {
			LOGGER.error(StrUtil.toOneLine("IllegalArgument"));
		} catch (AppRuntimeException e) {
			LOGGER.error(StrUtil.toOneLine("AppRuntimeException, Properties decode failed , pelase check the properties"));
		}

		return value1;
	}

}
