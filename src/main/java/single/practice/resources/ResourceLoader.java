package single.practice.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;
import single.practice.resources.ApplicationContextUtil;

import java.util.Locale;

@Service
public class ResourceLoader {

	public ResourceLoader() {
	}

	public static ResourceLoader getResourceLoader() {
		return (ResourceLoader) ApplicationContextUtil.getContext().getBean("resourceLoader");
	}

	@Autowired
	private ResourceBundleMessageSource messageResources;

	/**
	 * 读取配置
	 * @param key
	 * @param args
	 * @return
	 */
	public String loadValue(String key, Object... args) {
		return messageResources.getMessage(key, args, Locale.CHINA);
	}

}
