package single.practice.util;

import java.util.UUID;

/**
 * 
 * ID生成器
 * 
 * 
 */
public abstract class IDGenerator {

	/**
	 * 公用类不应该有public 的构造方法
	 * 公共类，这是一个集静态成员，不能被实例化。 即使是抽象的实用程序类，也可以扩展，不应该有公共构造函数。
	 * java添加一个隐式公共构造函数给每一个类。 因此，至少应该为公共类定义一个非公共构造函数
	 */
	private IDGenerator(){
	}

	/**
	 * 获取唯一的UUID，可作为数据的唯一标示
	 * 
	 * @return
	 */
	public static String getUUID() {

		String result = UUID.randomUUID().toString();
		result = result.replaceAll("-", "");
		return result;
	}

}
