 /**************************************************************************
 * Copyright (c) 2015-2017  Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-汇道体育
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *        情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *        识产权保护的内容。                            
 ***************************************************************************/

package com.zjtachao.hd.common.util.tools;


import java.io.File;
import java.io.FileInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jswiff.SWFReader;
import com.zjtachao.hd.common.video.swf.JPEGListener;
import com.zjtachao.hd.common.video.tools.Encoder;
import com.zjtachao.hd.common.video.tools.MultimediaInfo;
import com.zjtachao.hd.common.video.tools.VideoSize;

 /**
 * 视频工具类
 * @author <a href="mailto:zgf@zjtachao.com">zhuguofeng</a>
 * @version $Id$   
 * @since 2.0
 */

public class HdCommonVideoUtil {
	
	/** 日志 **/
	private static Logger logger = LoggerFactory.getLogger(HdCommonVideoUtil.class);	

	
	/**
	 * 
	   * 获取视频尺寸
	   * @param path
	   * @return
	 */
	public static VideoSize getVideoSize(String path){
		File file = new File(path);
		VideoSize videoSize = null;
		try {
			Encoder encoder = new Encoder();
			MultimediaInfo info = encoder.getInfo(file);
			videoSize = info.getVideo().getSize();
		}catch(Exception ex){
			logger.error("获取视频尺寸失败！" , ex);
		}
		return videoSize;
	}
	
	/**
	 * 
	   * 获得视频时长
	   * @param file
	   * @return
	 */
	public static long getVideoTimeLength(String path){
		long result = -1l;
		File file = new File(path);
		Encoder encoder = new Encoder();
		try{
			MultimediaInfo info = encoder.getInfo(file);
			if (null != info.getVideo() || null != info.getAudio()) {
				Long duration = info.getDuration() / 1000;
				result = duration;
			}
		}catch(Exception e){
			logger.error("获取视频时长错误！" ,e);
		}
		return result;
	}
	
	
	/**
	 * 
	   * 生成缩略图
	   * @param videoPath
	 */
	public static void getFirstFrame(String videoPath){
		VideoSize videoSize = getVideoSize(videoPath);
		try {
			String imgPath = videoPath.substring(0,videoPath.lastIndexOf("."))+".jpg";
			Encoder encoder = new Encoder();
			encoder.getFirstFrame(videoPath, imgPath, videoSize.getWidth(), videoSize.getHeight());
			
		}catch(Exception ex){
			logger.error("生成缩略图失败！" , ex);
		}
	}
	
	/**
	 * 
	   * 获取swf的第一帧
	   * @param videoPath
	 */
	public static void getSwfFirstFrame(String videoPath){
		try{
			String imgPath = videoPath.substring(0,videoPath.lastIndexOf("."))+".jpg";
			File sourceFile = new File(videoPath);
			SWFReader reader = new SWFReader(new FileInputStream(sourceFile));
			reader.addListener(new JPEGListener(imgPath));
			reader.read();
		}catch(Exception ex){
			logger.error("生成swf缩略图失败！" , ex);
		}
	}
}  


