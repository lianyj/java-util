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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;

 /**
 * 对象转换器
 * @author <a href="mailto:dh@zjtachao.com">duhao</a>
 * @version $Id$   
 * @since 2.0
 */
public class DozerBeanUtil {

	/** bean转换器 **/
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	
	/**
	 * 
	   * 对象转换器
	   * @param base
	   * @param target
	   * @return
	 */
	public <V , P> P convert(V base , Class<P> target){
		P p = null;
		if(null != base){
			p = this.dozerBeanMapper.map(base, target);
		}
		return p;
	}
	
	/**
	 * 
	   * 列表转换
	   * @param baseList
	   * @param target
	   * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <V , P> List<P> convertList(List<V> baseList , Class<P> target){
		List targetList = new ArrayList();
		if(null != baseList){
			for(Iterator iterator = baseList.iterator();iterator.hasNext();){
				Object vo = iterator.next();
				targetList.add(convert(vo, target));
			}
		}
		return targetList;
	}
	
	/**
	 * 对象转换：忽略值为null的属性
	 * @param base
	 * @param target
	 */
	public static <V , P> void copyPropertiesIgnoreNull(V source , P target){
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }
	
	/**
	 * 获取空的属性名称
	 * @param source
	 * @return
	 */
	public static <P> String[] getNullPropertyNames (P source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


	
	
	/**  
	 *@return  the dozerBeanMapper
	 */
	public DozerBeanMapper getDozerBeanMapper() {
		return dozerBeanMapper;
	}

	/** 
	 * @param dozerBeanMapper the dozerBeanMapper to set
	 */
	public void setDozerBeanMapper(DozerBeanMapper dozerBeanMapper) {
		this.dozerBeanMapper = dozerBeanMapper;
	}

}
