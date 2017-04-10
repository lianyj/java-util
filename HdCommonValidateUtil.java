/**************************************************************************
 * Copyright (c) 2015-2017  Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-汇道体育
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.zjtachao.hd.common.util.tools;

import java.io.File;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zjtachao.framework.common.util.tools.RegexValidateUtil;
import com.zjtachao.framework.common.util.tools.StringUtil;

/**
 * 公共验证
 * 
 * @author <a href="mailto:dh@zjtachao.com">zhuguofeng</a>
 * @version $Id$
 * @since 2.0
 */

public class HdCommonValidateUtil {
	
	/**
	 * 
	 * 正则验证
	 * 
	 * @param regex
	 * @param str
	 * @return
	 */
	private static boolean match(String regex, String str) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}
	
	/**
	 * 
	 * 验证邮箱是否正确
	 * 
	 * @param email
	 * @return
	 */
	public static boolean validateEmail(String email) {
		boolean flag = false;
		if ((null != email) && (!"".equals(email)) && (email.contains("@"))) {
			String[] emailArray = email.split("@");
			if ((null != emailArray) && (emailArray.length == 2)) {
				int before = StringUtil.getStringLength(emailArray[0]);				
				int end = StringUtil.getStringLength(emailArray[1]);
				if(before>0 && before<200 && end>0 && end <200){
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * 验证是否含有非法字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateStandStr(String str) {
		String regex = "^[a-zA-Z0-9\u4e00-\u9fa5]+$";
		boolean flag =  match(regex, str);
		return flag;
	}
	
	/**
	 * 
	 * 验证手机号
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validatePhoneStr(String str) {
		String regex = "^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$";
		return match(regex, str);
	}
	
	/**
	 * 
	   * 验证密码
	   * @param str
	   * @return
	 */
	public static boolean validateMd5Pwd(String str){
		boolean flag = false;
		if((null != str) && (validateStandStr(str)) && (StringUtil.getStringLength(str) == 32)){
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 
	   * 验证密码
	   * @param str
	   * @return
	 */
	public static boolean validatePwd(String str){
		boolean flag = false;
		if(StringUtil.getStringLength(str) >= 6 && StringUtil.getStringLength(str) <= 12){
			flag = true;
		}
		if(flag){
			String regex = "^[a-zA-Z0-9]+$";
			return match(regex, str);
		}
		return flag;
	}
	
	
	/**
	 * 
	   * 验证验证码
	   * @param str
	   * @return
	 */
	public static boolean validateCaptcha(String str){
		boolean flag = false;
		if((null != str) && (RegexValidateUtil.isNumber(str)) && StringUtil.getStringLength(str) == 6){
			flag = true;
		}
		return flag;
	}

	/**
	 * 
	 * 验证字符串
	 * 
	 * @param userName
	 * @return
	 */
	public static boolean validateUserName(String userName) {
		boolean flag = validateStandStr(userName);
		if (flag) {
			int count = StringUtil.getStringLength(userName);
			if (count >= 6 && count <= 20) {
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}
	
	/**
	 * 
	   * 保密字符串转换
	   * @param no
	   * @return
	 */
	public static String convertSecretno(String no , int length){
		String result = null;
		if((null != no) && (no.length()>length)){
			String begin = no.substring(0,no.length()-length-1);
			String end = no.substring(no.length()-1);
			String convert = "";
			for(int i=0 ; i<length;i++){
				convert += "*";
			}
			result = begin + convert + end;
		}
		return result;
	}
	
	/**
	 * 
	   * 手机号码 正则判断 
	   * @param str
	   * @return
	 */
	public static boolean isMobileLegal(String str) {  
        String regExp = "^((13[0-9])|(15[0-9])|(18[0-9])|(17[0-9])|(147))\\d{8}$";     
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }  
	
	
	/**
	 * 
	   * 验证验证码
	   * @param str
	   * @return
	 */
	public static boolean validateTelNumber(String str){
		boolean flag = false;
		if(null != str){
			Pattern pattern = Pattern.compile("((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)");
			if(null != str){
				Matcher match = pattern.matcher(str);
				if (match.matches()) {
					flag = true;
				} 
			}
		}
		return flag;
	}
	
	/**
	 * 
	   * 验证网址
	   * @param str
	   * @return
	 */
	public static boolean validateUrl(String str){
		boolean flag = false;
		if((null != str)){
			Pattern pattern = Pattern.compile("((^http)|(^https))://(w)+.(w)+");
			if(null != str){
				Matcher match = pattern.matcher(str);
				if (match.matches()) {
					flag = true;
				} 
			}
		}
		return flag;
	}

	/**
	 * 
	 * 验证图片类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean validateImgFileType(String fileName) {
		boolean flag = false;
		if ((null != fileName) && (!"".equals(fileName))) {
			int index = fileName.lastIndexOf(".");
			if (index > 0) {
				String type = fileName.substring(index + 1);
				if ((null != type)
						&& (type.equalsIgnoreCase("jpg")
								|| type.equalsIgnoreCase("png")
								|| type.equalsIgnoreCase("gif"))) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * 验证swf文件类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean validateSwfFileType(String fileName) {
		boolean flag = false;
		if ((null != fileName) && (!"".equals(fileName))) {
			int index = fileName.lastIndexOf(".");
			if (index > 0) {
				String type = fileName.substring(index + 1);
				if ((null != type) && (type.equalsIgnoreCase("swf"))) {
					flag = true;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * 验证flv文件类型
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean validateVideoFileType(String fileName) {
		boolean flag = false;
		if ((null != fileName) && (!"".equals(fileName))) {
			int index = fileName.lastIndexOf(".");
			if (index > 0) {
				String type = fileName.substring(index + 1);
				if ((null != type) && (type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("mpeg") 
						|| type.equalsIgnoreCase("mov")  || type.equalsIgnoreCase("flv")
						|| type.equalsIgnoreCase("avi"))) {
					flag = true;
				}
			}
		}
		return flag;
	}

	/**
	 * 
	 * 验证正整数
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateInteger(String str) {
		boolean flag = false;
		if ((null != str)) {
			Pattern pattern = Pattern.compile("^\\d+$");
			if (null != str) {
				Matcher match = pattern.matcher(str);
				if (match.matches()) {
					flag = true;
				}
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * 删除文件
	 * 
	 * @param sPath
	 * @return
	 */
	public static boolean deleteFile(String sPath) {
		boolean flag = false;
		File file = new File(sPath);
		// 路径为文件且不为空则进行删除
		if (file.isFile()) {
			file.delete();
			flag = true;
		}
		return flag;
	}
	
	/**
	 * 
	 * 验证ip地址
	 * @param ipAddress
	 * @return
	 */
	public static boolean isIp(String ipAddress){
		String  ip="(2[5][0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\." +
				"(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})\\.(25[0-5]|2[0-4]\\d|1\\d{2}|\\d{1,2})";
		 Pattern pattern = Pattern.compile(ip); 
         Matcher matcher = pattern.matcher(ipAddress);   
         return matcher.matches();
	}
	
	public static String getFileSize(long fileSize){
		String fileSizeStr = "";
		DecimalFormat df = new DecimalFormat("#.0");
	       if (fileSize < 1024) {
	    	   fileSizeStr = df.format((double) fileSize) + "B";
	       } else if (fileSize < 1048576) {
	    	   fileSizeStr = df.format((double) fileSize / 1024) + "K";
	       } else if (fileSize < 1073741824) {
	    	   fileSizeStr = df.format((double) fileSize / 1048576) + "M";
	       } else {
	    	   fileSizeStr = df.format((double) fileSize / 1073741824) +"G";
	       }
		return fileSizeStr;
	}
	
	/**
	 * 
	 * 配置是否是数字和字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateNumberAndLetter(String str) {
		String regex = "[A-Za-z0-9]+";
		return match(regex, str);
	}
	
	/**
	 * 
	 * 配置是否是数字
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateNumber(String str) {
		String regex = "[0-9]+";
		return match(regex, str);
	}
	
	/**
	 * 
	 * 配置是否是大写字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateLargeLetter(String str) {
		String regex = "[A-Z]+";
		return match(regex, str);
	}
	
	/**
	 * 
	 * 配置是否字母
	 * 
	 * @param str
	 * @return
	 */
	public static boolean validateLetter(String str) {
		String regex = "[A-Za-z]+";
		return match(regex, str);
	}
	
}
