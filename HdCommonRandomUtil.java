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

import java.util.Random;

 /**
 * 随机数工具类
 * @author <a href="mailto:zgf@zjtachao.com">zhuguofeng</a>
 * @version $Id$   
 * @since 2.0
 */

public class HdCommonRandomUtil {
	
	/**
	 * 
	 * 生成六位随机数
	 * @return
	 */
	public static String random(){
		
		StringBuffer buffer = new StringBuffer();
		//添加三位随机数
        //生成三个 0-9
        int num1, num2, num3, num4, num5, num6;
        Random rnd = new Random();
        num1 = rnd.nextInt(9);
        num2 = rnd.nextInt(9);
        num3 = rnd.nextInt(9);
        num4 = rnd.nextInt(9);
        num5 = rnd.nextInt(9);
        num6 = rnd.nextInt(9);
        
        String num=num1+""+num2+""+num3+""+num4+""+num5+""+num6;            
	    buffer.append(num);
	    
	    return buffer.toString();
	}

}
