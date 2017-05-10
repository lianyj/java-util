 /**************************************************************************
 * Copyright (c) 2015-2016  Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-天添彩-管理后台
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.zjtachao.framework.service.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import com.zjtachao.framework.common.util.tools.XStreamUtil;
import com.zjtachao.framework.pojo.dto.redis.RedisDto;

 /**
 * RedisService实现类
 * @author <a href="mailto:zy@zjtachao.com">zhouyang</a>
 * @version $Id$   
 * @since 2.0
 */
public class TtcRedis {
	
	/** 日志对象 **/
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/** RedisTemplate **/
	@Resource
	private RedisTemplate<String,Object> redisTemplate;
	
	
	/**
	 * 增加或修改redis缓存对象
	 * @param redisDto
	 */
	public void addOrUpdate(RedisDto redisDto) {
		try {
			if (null != redisDto) {
				String value = XStreamUtil.getXStream().toXML(redisDto.getObject());
				redisTemplate.opsForValue().set(redisDto.getKey(), value);
				expireTime(redisDto);
				expireDate(redisDto);
			}
		} catch (Exception e) {
			logger.error("增加或修改redis缓存对象出错，错误信息为："+e.getMessage(), e);
		}
	}
	
	
	/**
	 * 查询redis缓存字符串
	 * @param key
	 * @return
	 */
	public String queryString(String key) {
		try {
			return (String) redisTemplate.opsForValue().get(key);
		} catch (Exception e) {
			logger.error("查询redis缓存字符串出错，错误信息为："+e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * 查询redis缓存对象
	 * @param key
	 * @return
	 */
	public Object queryObject(String key) {
		Object object = null;
		try {
			object = redisTemplate.opsForValue().get(key);
			if (null != object) {
				object = XStreamUtil.getXStream().fromXML((String) object);
			}
		} catch (Exception e) {
			logger.error("查询redis缓存对象出错，错误信息为："+e.getMessage(), e);
		}
		return object; 
	}
	
	
	/**
	 * 获取key集合
	 * @param pattern
	 */
	public Set<String> getKeys(String pattern) {
		try {
			return redisTemplate.keys(pattern);
		} catch (Exception e) {
			logger.error("获取key集合出错，错误信息为："+e.getMessage(), e);
		}
		return null;
	}
	
	
	/**
	 * 删除指定Key的缓存记录
	 * @param key
	 */
	public void delete(String key) {
		try {
			redisTemplate.delete(key);
		} catch (Exception e) {
			logger.error("删除指定Key的缓存记录出错，错误信息为："+e.getMessage(), e);
		}
	}
	
	
	/**
	 * 删除key集合对应的redis缓存
	 * @param keys
	 */
	public void delete(List<String> keys) {
		try {
			redisTemplate.delete(keys);
		} catch (Exception e) {
			logger.error("删除key集合对应的redis缓存出错，错误信息为："+e.getMessage(), e);
		}
	}
	

	/**
	 * 判断key是否存在
	 * @param key
	 */
	public boolean exists(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			logger.error("判断key是否存在出错，错误信息为："+e.getMessage(), e);
		}
		return false;
	}

	
	/**
	 * 设置过期时间
	 * @param object
	 */
	public void expireTime(RedisDto object) {
		Long minute = object.getMinute();
		if (null != minute && !("").equals(minute)) {
			redisTemplate.expire(object.getKey(), minute, TimeUnit.MINUTES);
		}		
	}
	
	/**
	 * 设置过期时间
	 * @param object
	 */
	public void expireTime(String key , long minute) {
		redisTemplate.expire(key, minute, TimeUnit.MINUTES);	
	}
	
	/**
	 * 
	   * 获得对象过期时间
	   * @param key
	   * @return
	 */
	public Long getExpireTimeMinutes(String key){
		return redisTemplate.getExpire(key, TimeUnit.MINUTES);
	}
	
	
	/**
	 * 设置过期日期
	 * @param object
	 */
	public void expireDate(RedisDto object) {
		Date date = object.getDate();
		if (null != date) {
			redisTemplate.expireAt(object.getKey(), date);
		}
	}
	
	
	/**
	 * 清空redis库
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String flushDB() {
		try {
			return redisTemplate.execute(new RedisCallback() {
			    public String doInRedis(RedisConnection connection) throws DataAccessException {
			        connection.flushDb();
			        return "ok";
			    }
			});
		} catch (Exception e) {
			logger.error("清空redis库出错，错误信息为："+e.getMessage(), e);
		}
		return null;
	}

	
	/**
	 * 获取redis库中的数据数量
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public long dbSize() {
		try {
			return redisTemplate.execute(new RedisCallback() {
				public Object doInRedis(RedisConnection connection) throws DataAccessException {
					return connection.dbSize();
				}
			});
		} catch (Exception e) {
			logger.error("获取redis库中的数据数量出错，错误信息为："+e.getMessage(), e);
		}
		return 0l;
	}
	
	/**
	 * 
	   * 获得redis的key类型
	   * @param key
	   * @return
	 */
	public DataType type(String key){
		DataType dataType = redisTemplate.type(key);
		return dataType;
	}
	
	/**
	 * 
	   * 设置普通对象
	   * @param key
	   * @param value
	 */
	public void set(String key , Object value){
		redisTemplate.opsForValue().set(key, value);
	}
	
	 /** 过期时间参数
	   * @param key
	   * @param value
	   * @param expireDate
	 */
	public void set(String key , Object value , Date expireDate){
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expireAt(key, expireDate);
	}
	

	/**
	 * 
	   * 过期时间参数
	   * @param key
	   * @param value
	   * @param seconds
	 */
	public void set(String key , Object value , long seconds){
		redisTemplate.opsForValue().set(key, value);
		redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
	}
	


	
	/**
	 * 
	   * 设置普通对象
	   * @param key
	   * @param value
	 */
	public Object get(String key){
		return redisTemplate.opsForValue().get(key);
	}
	
	/**
	 * 
	   * 原子性处理数据
	   * @param key
	   * @param value
	   * @return
	 */
	public long increment(String key , long value){
		return redisTemplate.opsForValue().increment(key, value);
	}
	
	/**
	 * 
	   * 原子性处理数据
	   * @param key
	   * @param value
	   * @return
	 */
	public long increment(String key , long value , long minute){
		long result = redisTemplate.opsForValue().increment(key, value);
		redisTemplate.expire(key, minute, TimeUnit.MINUTES);
		return result;
	}
	
	/**
	 * 
	   * 原子性处理数据
	   * @param key
	   * @param value
	   * @return
	 */
	public long increment(String key , long value , Date date){
		long result = redisTemplate.opsForValue().increment(key, value);
		redisTemplate.expireAt(key, date);
		return result;
	}

	/**
	 * 
	   * set新增
	   * @param key
	   * @param value
	   * @return
	 */
	public long sadd(String key , String... values){
		long result = redisTemplate.opsForSet().add(key, values);
		return result;
	}
	
	
	/**
	 * 
	   * set删除
	   * @param key
	   * @param value
	   * @return
	 */
	public long srem(String key , String... values){
		long result = redisTemplate.opsForSet().remove(key, values);
		return result;
	}
	

	/**
	 * 
	   * 获得set的成员
	   * @param key
	   * @return
	 */
	public Set<Object> smembers(String key){
		Set<Object> result = redisTemplate.opsForSet().members(key);
		return result;
	}
	
	
	/**
	 * 
	   * set交集
	   * @param key
	   * @param value
	   * @return
	 */
	public Set<Object> sinter(String requeKey , List<String> otherkeys){
		Set<Object> result = redisTemplate.opsForSet().intersect(requeKey, otherkeys);
		return result;
	}
	
	/**
	 * 
	 * Hash新增
	 * @param redisDto
	 */
	public void hset(RedisDto redisDto) {
		redisTemplate.opsForHash().put(redisDto.getKey(), redisDto.getHashKey(), redisDto.getObject());
		expireTime(redisDto);
		expireDate(redisDto);
	}
	
	/**
	 * 
	 * Hash新增
	 * @param redisDto
	 */
	public void hset(String key, String hashKey , String value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}
	
	/**
	 * 获取Hash值
	 * @param key
	 * @param hashKey
	 * @return
	 */
	public Object hget(String key, String hashKey) {
		Object object = redisTemplate.opsForHash().get(key, hashKey);
		return object;
	}
	
	/**
	 * 获取Hash值
	 * @param redisDto
	 * @return
	 */
	public Object hget(RedisDto redisDto) {
		Object object = redisTemplate.opsForHash().get(redisDto.getKey(), redisDto.getHashKey());
		return object;
	}
	
	/**
	 * 
	   * 获得所有广告key
	   * @param key
	   * @return
	 */
	public Set<Object> hkeys(String key){
		Set<Object> sets = redisTemplate.opsForHash().keys(key);
		return sets;
	}
	
	/**
	 * 
	   * 获得所有广告key
	   * @param key
	   * @return
	 */
	public Map<Object , Object> hall(String key){
		Map<Object , Object> maps = redisTemplate.opsForHash().entries(key);
		return maps;
	}
	
	/**
	 * 
	   * 删除数据
	   * @param key
	   * @param hashKey
	 */
	public void hdel(String key , String hashKey){
		redisTemplate.opsForHash().delete(key, hashKey);
	}
	
	
	
	/**
	 * 
	 * 在列表后面新增
	 * @param redisDto
	 */
	public void lRightPush(RedisDto redisDto) {
		redisTemplate.opsForList().rightPush(redisDto.getKey(), redisDto.getObject());
		expireTime(redisDto);
		expireDate(redisDto);
	}
	
	/**
	 * 
	 * 通过索引获取列表中的元素
	 * @param key
	 */
	public Object lindex(String key, int index) {
		Object object = redisTemplate.opsForList().index(key, index);
		return object;
	}
	
	/**
	 * 
	 * 获取列表中元素个数
	 * @param key
	 */
	public Long lsize(String key) {
		Long size = redisTemplate.opsForList().size(key);
		return size;
	}
	
	/**
	 * 
	 * 移除并获取列表的第一个元素
	 * @param key
	 */
	public void lpop(String key) {
		redisTemplate.opsForList().leftPop(key);
	}
	
	/**
	 * 
	   * 获得过期时间
	   * @param key
	   * @return
	 */
	public Long ttl(String key){
		return redisTemplate.getExpire(key);
	}
}
