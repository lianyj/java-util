
package com.zjtachao.framework.common.util.tools;

import org.springframework.beans.factory.annotation.Autowired;


 /**
 * 配置文件读取
 * @author <a href="mailto:dh@zjtachao.com">duhao</a>
 * @version $Id$   
 * @since 2.0
 */
public class ConfigUtil {	

	/** bean转换器 **/
	@Autowired
	private TtcPropertyPlaceholderConfigurer ttcPropertyPlaceholderConfigurer;

	/** 获得配置文件 **/
	public String getConfigByKey(String key){
		return ttcPropertyPlaceholderConfigurer.getMap().get(key);
	}

	/**  
	 *@return  the ttcPropertyPlaceholderConfigurer
	 */
	
	public TtcPropertyPlaceholderConfigurer getTtcPropertyPlaceholderConfigurer() {
		return ttcPropertyPlaceholderConfigurer;
	}

	/** 
	 * @param ttcPropertyPlaceholderConfigurer the ttcPropertyPlaceholderConfigurer to set
	 */
	public void setTtcPropertyPlaceholderConfigurer(
			TtcPropertyPlaceholderConfigurer ttcPropertyPlaceholderConfigurer) {
		this.ttcPropertyPlaceholderConfigurer = ttcPropertyPlaceholderConfigurer;
	}
	
}
