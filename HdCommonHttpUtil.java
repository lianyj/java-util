 /**************************************************************************
 * Copyright (c) 2006-2015 Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-富春媒体对接平台
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.zjtachao.hd.common.util.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


 /**
 * HttpUtil
 * @author <a href="mailto:dh@zjtachao.com">duhao</a>
 * @version $Id$   
 * @since 2.0
 */
public class HdCommonHttpUtil {
	
	/** 日志 **/
	private static Logger logger = LoggerFactory.getLogger(HdCommonHttpUtil.class);	
	
	/**
	 * HttpPost
	 * @param postUrl
	 * @param jsonContent
	 * @return 结果
	 */
	public static String httpPost(String postUrl, String jsonContent){
		String result = null;
		HttpPost postRequest = null;
		CloseableHttpResponse response = null;
		try {
			StringBuffer buffer = new StringBuffer();
			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
			postRequest = new HttpPost(postUrl);
			StringEntity input = new StringEntity(jsonContent, StandardCharsets.UTF_8.name());
			input.setContentType("application/json;charset=UTF-8");
			postRequest.setEntity(input);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			postRequest.setConfig(requestConfig);
			
			HttpContext localContext = new BasicHttpContext();			
			response = httpClient.execute(postRequest , localContext);
			
			if((null != response) && (null != response.getStatusLine())
					&& (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
				
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
						StandardCharsets.UTF_8.name()));
				String output;
				
				while ((output = br.readLine()) != null) {
					buffer.append(output);				
				}

				try{  
				  HttpEntity entity = response.getEntity();  
				  if(entity!=null){  
					  InputStream instream = entity.getContent();  
					  instream.close();  
				  }  
				} finally{
					postRequest.releaseConnection();
					response.close();
				}  
				result = buffer.toString();//new String(buffer.toString().getBytes() , StandardCharsets.UTF_8.name());
			}else {
				logger.warn("response: " + response.getStatusLine().getStatusCode());
			}
		} catch (MalformedURLException e) {
			logger.error("响应错误！" , e);			
		} catch (IOException e) {
			logger.error("响应错误！" , e);
		} finally{
			if(null != postRequest){
				postRequest.releaseConnection();
			}
			if(null != response){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭连接错误！" , e);
				}
			}
		}
		return result;
	}
	
	/**
	 * HttpPost
	 * @param postUrl
	 * @param jsonContent
	 * @return 结果
	 */
	public static String httpPost(String postUrl, Map<String , String> params){
		String result = null;
		HttpPost postRequest = null;
		CloseableHttpResponse response = null;
		try {
			StringBuffer buffer = new StringBuffer();
			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
			postRequest = new HttpPost(postUrl);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			if((null != params) && !params.isEmpty()){
				for(String key : params.keySet()){
					list.add(new BasicNameValuePair(key,params.get(key)));  
				}
			}
			
			UrlEncodedFormEntity urlEntity = new UrlEncodedFormEntity(list,StandardCharsets.UTF_8); 
			postRequest.setEntity(urlEntity);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000).setConnectTimeout(3000).build();
			postRequest.setConfig(requestConfig);
			
			HttpContext localContext = new BasicHttpContext();			
			response = httpClient.execute(postRequest , localContext);
			
			if((null != response) && (null != response.getStatusLine())
					&& (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
				
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
						StandardCharsets.UTF_8.name()));
				String output;
				
				while ((output = br.readLine()) != null) {
					buffer.append(output);				
				}

				try{  
				  HttpEntity entity = response.getEntity();  
				  if(entity!=null){  
					  InputStream instream = entity.getContent();  
					  instream.close();  
				  }  
				} finally{
					postRequest.releaseConnection();
					response.close();
				}  
				result = buffer.toString();//new String(buffer.toString().getBytes() , StandardCharsets.UTF_8.name());
			}else {
				logger.warn("response: " + response.getStatusLine().getStatusCode());
			}
		} catch (MalformedURLException e) {
			logger.error("响应错误！" , e);			
		} catch (IOException e) {
			logger.error("响应错误！" , e);
		} finally{
			if(null != postRequest){
				postRequest.releaseConnection();
			}
			if(null != response){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭连接错误！" , e);
				}
			}
		}		
		return result;
	}
	
	
	/**
	 * HttpPost
	 * @param postUrl
	 * @param jsonContent
	 * @return 结果
	 */
	public static String httpFmobiPost(String postUrl, String jsonContent , String token){
		String result = null;
		HttpPost postRequest = null;
		CloseableHttpResponse response = null;
		try {
			StringBuffer buffer = new StringBuffer();
			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
			postRequest = new HttpPost(postUrl);
			postRequest.addHeader("Content-Type", "application/json");
			postRequest.addHeader("X-TOKEN", token);
			
			StringEntity input = new StringEntity(jsonContent, StandardCharsets.UTF_8.name());
			input.setContentType("application/json;charset=UTF-8");
			postRequest.setEntity(input);
			
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			postRequest.setConfig(requestConfig);
			
			HttpContext localContext = new BasicHttpContext();			
			response = httpClient.execute(postRequest , localContext);
			
			if((null != response) && (null != response.getStatusLine())
					&& (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
				
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
						StandardCharsets.UTF_8.name()));
				String output;
				
				while ((output = br.readLine()) != null) {
					buffer.append(output);				
				}

				try{  
				  HttpEntity entity = response.getEntity();  
				  if(entity!=null){  
					  InputStream instream = entity.getContent();  
					  instream.close();  
				  }  
				} finally{
					postRequest.releaseConnection();
					response.close();
				}  
				result = buffer.toString();//new String(buffer.toString().getBytes() , StandardCharsets.UTF_8.name());
			}else {
				logger.warn("response: " + response.getStatusLine().getStatusCode());
			}
		} catch (MalformedURLException e) {
			logger.error("响应错误！" , e);			
		} catch (IOException e) {
			logger.error("响应错误！" , e);
		} finally{
			if(null != postRequest){
				postRequest.releaseConnection();
			}
			if(null != response){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭连接错误！" , e);
				}
			}
		}
		return result;
	}
	
	

	/**
	 * @param httpUrl
	 * @return
	 */
	public static String httpGet(String httpUrl) {
		StringBuffer buffer = new StringBuffer();
		HttpGet getRequest = null;
		CloseableHttpResponse response = null;
		try {
			CloseableHttpClient httpClient = HttpClientBuilder.create().build(); 
			getRequest = new HttpGet(httpUrl);
			getRequest.addHeader("accept", "application/json");
			//设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();
			getRequest.setConfig(requestConfig);
			response = httpClient.execute(getRequest);
			
			if((null != response) && (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)){
				BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),StandardCharsets.UTF_8.name()));
				String output;				
				while ((output = br.readLine()) != null) {
					buffer.append(output);					
				}
				try {
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						InputStream instream = entity.getContent();
						instream.close();
					}
				} finally {
					getRequest.releaseConnection();
					response.close();
				}
			}
		} catch (ClientProtocolException e) {
			logger.error("响应错误！" , e);
		} catch (IOException e) {
			logger.error("响应错误！" , e);
		} finally{
			if(null != getRequest){
				getRequest.releaseConnection();
			}
			if(null != response){
				try {
					response.close();
				} catch (IOException e) {
					logger.error("关闭连接错误！" , e);
				}
			}
		}
		return buffer.toString();
	}
	
}
