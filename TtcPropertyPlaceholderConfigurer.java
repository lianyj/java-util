 /**************************************************************************
 * Copyright (c) 2006-2015 Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-基础架构
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.zjtachao.framework.common.util.tools;

import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

 /**
 * 天添彩属性管理类
 * @author <a href="mailto:dh@zjtachao.com">duhao</a>
 * @version $Id$   
 * @since 2.0
 */
public class TtcPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{
	
	/** 日志 **/
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**配置文件key\value的存储内存表*/
	private Map<String,String> map = new java.util.HashMap<String,String>();
	
	/**
	 * 覆盖父类方法,把相关信息填充到哈希表中
	 * @param factory factory
	 * @param pro pro
	 * @return
	 * @throws BeansException BeansException
	 */
	@SuppressWarnings({ "rawtypes", "deprecation" })
	@Override
	protected void processProperties(ConfigurableListableBeanFactory factory,
			Properties pro) throws BeansException {
		java.util.Enumeration<?> e = pro.propertyNames();
		while(e.hasMoreElements()){
			String key = (String)e.nextElement();
			String value = parseStringValue((String)pro.getProperty(key),pro,new java.util.HashSet());
			map.put(key, value);
		}
		for(java.util.Iterator<?> it = map.keySet().iterator();it.hasNext();){
			String key  = (String)it.next();
			logger.debug("key:{}:{}",key,map.get(key));
		}
		super.processProperties(factory, pro);
	}
	
	/**
	 * 获得配置文件key\value的存储内存表
	 * @return Map
	 */
	public Map<String, String> getMap() {
		return map;
	}

}
