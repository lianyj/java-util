 /**************************************************************************
 * Copyright (c) 2015-2017  Zhejiang TaChao Network Technology Co.,Ltd.
 * All rights reserved.
 * 
 * 项目名称：浙江踏潮-汇道体育
 * 版权说明：本软件属浙江踏潮网络科技有限公司所有，在未获得浙江踏潮网络科技有限公司正式授权
 *        情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *        识产权保护的内容。                            
 ***************************************************************************/

package com.zjtachao.hd.data.rest.resource.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.zjtachao.framework.common.util.json.JsonObject.ResultCode;
import com.zjtachao.framework.common.util.rest.RestContents;
import com.zjtachao.framework.common.util.tools.DateUtil;
import com.zjtachao.framework.common.util.tools.UUIDUtil;
import com.zjtachao.framework.pojo.rest.RestObject;
import com.zjtachao.hd.common.pojo.domain.pay.HdCommonPayDetail;
import com.zjtachao.hd.common.pojo.domain.user.HdCommonUserCardCoupon;
import com.zjtachao.hd.common.pojo.ro.card.HdCommonCardCouponParamRo;
import com.zjtachao.hd.common.pojo.ro.match.HdCommonMatchBaseInfoRo;
import com.zjtachao.hd.common.pojo.ro.pay.HdCommonPayDetailRo;
import com.zjtachao.hd.common.pojo.ro.user.HdCommonUserBaseInfoRo;
import com.zjtachao.hd.common.pojo.so.match.HdCommonMatchBaseInfoSo;
import com.zjtachao.hd.common.pojo.so.pay.HdCommonPayDetailSo;
import com.zjtachao.hd.common.pojo.so.user.HdCommonUserBaseInfoSo;
import com.zjtachao.hd.common.rest.resoures.base.HdCommonBaseResource;
import com.zjtachao.hd.common.service.card.HdCommonCardCouponParamService;
import com.zjtachao.hd.common.util.constants.HdCommonConstants;
import com.zjtachao.hd.common.util.constants.HdCommonWxPayConstants;
import com.zjtachao.hd.common.util.constants.HdCommonWxPayConstants.SignType;
import com.zjtachao.hd.common.util.context.HdCommonContext;
import com.zjtachao.hd.common.util.tools.HdCommonIdWorkerUtil;
import com.zjtachao.hd.common.util.tools.HdCommonUniqueIdUtil;
import com.zjtachao.hd.common.util.tools.HdCommonWxPayUtil;
import com.zjtachao.hd.data.pojo.beans.HdDataAliPayRespBean;
import com.zjtachao.hd.data.pojo.beans.HdDataWechatPayRespBean;
import com.zjtachao.hd.data.service.match.HdDataMatchBaseInfoService;
import com.zjtachao.hd.data.service.pay.HdDataPayDetailService;
import com.zjtachao.hd.data.service.user.HdDataUserBaseInfoService;
import com.zjtachao.hd.data.service.user.HdDataUserCardCouponService;
import com.zjtachao.hd.data.util.constants.HdDataCommonConstant;

 /**
 * 支付明细接口
 * @author <a href="mailto:zgf@zjtachao.com">zhuguofeng</a>
 * @version $Id$   
 * @since 2.0
 */
@Path("/admin/pay/detail")
public class HdDataPayDetailResource extends HdCommonBaseResource{
	
	/** 支付明细Service */
	@Autowired
	private HdDataPayDetailService hdDataPayDetailService;
	
	/** 用户基本信息Service */
	@Autowired
	private HdDataUserBaseInfoService hdDataUserBaseInfoService;
	
	/** 卡券参数信息Service */
	@Autowired
	private HdCommonCardCouponParamService hdCommonCardCouponParamService;
	
	/** 卡券基本信息Service */
	@Autowired
	private HdDataUserCardCouponService hdDataUserCardCouponService;
	
	/** 赛事基本信息Service */
	@Autowired
	private HdDataMatchBaseInfoService hdDataMatchBaseInfoService;
	
	/**
	 * 
	 * 新增支付明细
	 * @param jsonStr
	 * @param sessionId
	 * @param userAgent
	 * @param jsonpCallback
	 * @return
	 */
	@POST
	@Path("/order/create")
	@Produces(RestContents.MEDIA_TYPE_APPLICATION_JSON_UTF8)
	public Object createPayDetail(String jsonStr,
			@CookieParam(HD_LOGIN_USER_COOKIE_KEY) String userCookieKey,
			@HeaderParam("User-Agent") String userAgent,
			@Context HttpServletRequest request,
			@QueryParam("jsonpCallback")String jsonpCallback){
		RestObject<Object> rest = new RestObject<Object>();
		rest.setCode(ResultCode.VALID_NO_PASS.getCode());
		try{	
			boolean flag = true;
		
			if(null == jsonStr || "".equals(jsonStr) || jsonStr.isEmpty()){
				flag = false;
				rest.setMsg("支付参数不能为空！");
			}
			
			if(flag){
				HdCommonPayDetail payDetail = JSON.parseObject(jsonStr, HdCommonPayDetail.class);
			
				//参数验证
				if(flag && ((null == payDetail.getPayWay()))){
					flag = false;
					rest.setMsg("请选择支付方式！");
				}
				
				if(flag && ((null == payDetail.getSaleCode()))){
					flag = false;
					rest.setMsg("售卖方编码不能为空！");
				}
				
				if(flag && ((null == payDetail.getAmount()))){
					flag = false;
					rest.setMsg("金额不能为空！");
				}
				
				if(flag && ((null == payDetail.getOrderSubject()) || ("".equals(payDetail.getOrderSubject())))){
					flag = false;
					rest.setMsg("订单标题不能为空！");
				}
				
				if(flag && ((null == payDetail.getOrderDesc()) || ("".equals(payDetail.getOrderDesc())))){
					flag = false;
					rest.setMsg("订单描述不能为空！");
				}
				
				if(flag && ((null == payDetail.getBuyType()) || ("".equals(payDetail.getBuyType())))){
					flag = false;
					rest.setMsg("购买类型不能为空！");
				}
				
				if(flag){
					String userCode = getLoginCode(userCookieKey);
					//String userCode = "111111";//用户编码暂时写定
					if(payDetail.getBuyType().intValue() == HdCommonContext.buyTypeContext.CARD.getCode()){
						//生成卡券编码
						String cardCode = HdCommonUniqueIdUtil.genrateCode();
						payDetail.setGoodsNumber(cardCode);
						
						HdCommonUserBaseInfoSo userBaseInfoSo = new HdCommonUserBaseInfoSo();
						userBaseInfoSo.setUserCode(userCode);
						HdCommonUserBaseInfoRo userBaseInfoRo = hdDataUserBaseInfoService.queryDataUserInfo(userBaseInfoSo);
						if(null != userBaseInfoRo){
							//用户id
							Long userId= userBaseInfoRo.getId();
							//入场券
							String sortCode = HdCommonContext.CardCouponTypeContext.ENTRANCE.getCode();
							//查询入场券
							HdCommonCardCouponParamRo cardCouponParamRo = hdCommonCardCouponParamService.queryCommonCardCouponParamBySortCode(sortCode);
							if(null != cardCouponParamRo){
								HdCommonUserCardCoupon userCardCoupon = new HdCommonUserCardCoupon();
								userCardCoupon.setCardGeneralCode(cardCouponParamRo.getGeneralCode());
								userCardCoupon.setCardSortCode(sortCode);
								userCardCoupon.setCardCode(cardCode);
								userCardCoupon.setStartValidTime(cardCouponParamRo.getStartValidTime());
								userCardCoupon.setEndValidTime(cardCouponParamRo.getEndValidTime());
								userCardCoupon.setCardStatus(HdCommonContext.CardCouponStatusContext.UN_RECEIVE.getCode());
								userCardCoupon.setUserId(userId);
								hdDataUserCardCouponService.createUserCardCoupon(userCardCoupon);
							}
						}
						
					}else if(payDetail.getBuyType().intValue() == HdCommonContext.buyTypeContext.TICKET.getCode()){
						if(null == payDetail.getTicketNum()){
							flag = false;
							rest.setMsg("请选择购买数量");
						}
						
						if(flag && null == payDetail.getMatchCode()){
							flag = false;
							rest.setMsg("请选择对应的赛事");
						}
						
						if(flag){
							String matchCardParam[] = payDetail.getMatchCode().split("-");
							String matchCode = matchCardParam[0];
							//赛事编码
							payDetail.setMatchCode(matchCode);
							
							//门票类型
							String ticketType = matchCardParam[1];
							
							HdCommonMatchBaseInfoSo matchBaseInfoSo = new HdCommonMatchBaseInfoSo();
							matchBaseInfoSo.setMatchCode(matchCode);
							HdCommonMatchBaseInfoRo matchBaseInfoRo = hdDataMatchBaseInfoService.queryOneMatchInfoBySo(matchBaseInfoSo);
							if(null != matchBaseInfoRo && null != matchBaseInfoRo.getSureTime()){
								//时间转化
								String matchTimeStr = DateUtil.date2Str(matchBaseInfoRo.getSureTime(), "yyyy-MM-dd");
								
								String sortCode = HdCommonConstants.MATCH_TICKER_PREFIX + "_" + matchTimeStr.replaceAll("-", "");
								sortCode = sortCode+"_"+ticketType;
								
								HdCommonUserBaseInfoSo userBaseInfoSo = new HdCommonUserBaseInfoSo();
								userBaseInfoSo.setUserCode(userCode);
								HdCommonUserBaseInfoRo userBaseInfoRo = hdDataUserBaseInfoService.queryDataUserInfo(userBaseInfoSo);
								if(null != userBaseInfoRo){
									//用户id
									Long userId= userBaseInfoRo.getId();
									
									HdCommonCardCouponParamRo cardCouponParamRo = hdCommonCardCouponParamService.queryCommonCardCouponParamBySortCode(sortCode);
									if(null != cardCouponParamRo){
										//创建卡券
										Integer genCode = cardCouponParamRo.getGeneralCode();
										Date startValidTime = cardCouponParamRo.getStartValidTime();
										Date endValidTime = cardCouponParamRo.getEndValidTime();
										Integer cardStatus = HdCommonContext.CardCouponStatusContext.UN_RECEIVE.getCode();
										
										HdCommonUserCardCoupon userCardCoupon = new HdCommonUserCardCoupon();
										userCardCoupon.setCardSortCode(cardCouponParamRo.getSortCode());
										userCardCoupon.setCardGeneralCode(genCode);
										userCardCoupon.setCardStatus(cardStatus);
										userCardCoupon.setStartValidTime(startValidTime);
										userCardCoupon.setEndValidTime(endValidTime);
										userCardCoupon.setUserId(userId);
										
										StringBuffer cardCodeArr = new StringBuffer();
										for(int i = 0; i < payDetail.getTicketNum(); i++) {
											//卡券编码
											String cardCode = HdCommonUniqueIdUtil.genrateCode();
											userCardCoupon.setCardCode(cardCode);
											//生成卡券
											hdDataUserCardCouponService.createUserCardCoupon(userCardCoupon);
											cardCodeArr.append(cardCode+HdCommonConstants.HD_IMG_COMMA);
										}
										String cardCodeStr = cardCodeArr.toString().substring(0, cardCodeArr.toString().length()-1);
										payDetail.setGoodsNumber(cardCodeStr);
									}
								}
							}
						}
						
					}else{
						String goodsNumber = UUIDUtil.getUUID();
						payDetail.setGoodsNumber(goodsNumber);
					}

					
					Date date = new Date();
					//设置App端调起微信支付接口需要的时间戳
					String timestamp = String.valueOf(date.getTime()/1000);
					
					payDetail.setUserCode(userCode);
					String orderNum = HdCommonIdWorkerUtil.getOrderNo();
					payDetail.setOrderNumber(orderNum);
					payDetail.setTradeCreateTime(date);
					//设置交易过期时间
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					cal.add(Calendar.MINUTE, 30);
					Date tradeExpireTime = cal.getTime();
					payDetail.setTradeExpireTime(tradeExpireTime);
					payDetail.setPayStatus(HdCommonContext.PayStatusContext.SELLER_CREATE.getCode());
					payDetail.setPaySource("APP");
					
					if(payDetail.getPayWay().equals(HdCommonContext.PayWayContext.ALIPAY.getCode())){
						
				        AlipayTradeAppPayResponse response = excuteAliPay(payDetail);
				        if(null != response && null != response.getBody() && !"".equals(response.getBody())){
				        	HdDataAliPayRespBean alipayRespBean = new HdDataAliPayRespBean();
				        	payDetail.setPayStatus(HdCommonContext.PayStatusContext.INVOKE_SUCCESS.getCode());
				        	String responseParam = response.getBody();
				        	alipayRespBean.setBody(responseParam);
				        	alipayRespBean.setOrderNumber(orderNum);
				        	hdDataPayDetailService.createPayDetail(payDetail);
				        	rest.setCode(ResultCode.SUCCESS.getCode());
							rest.setMsg("支付明细创建成功！");
							rest.setRst(alipayRespBean);
				        }else{
				        	rest.setMsg("调用支付宝发起订单失败！");
				        }
				        
					}else if(payDetail.getPayWay().equals(HdCommonContext.PayWayContext.WECHAT_PAY.getCode())){
						
						//微信返回的xml格式的参数
						String strXml = excuteWechatPay(payDetail, request);
						//转成Map
						Map<String, String> map = HdCommonWxPayUtil.xmlToMap(strXml);
						if(null != map.get("return_code") && map.get("return_code").equals("SUCCESS") 
								&& null != map.get("result_code") && map.get("result_code").equals("SUCCESS")){
							String jsonObject = JSON.toJSONString(map, true);
							
							//转成App端需要的对象
							HdDataWechatPayRespBean respBean = JSON.parseObject(jsonObject, HdDataWechatPayRespBean.class);
							respBean.setTimestamp(timestamp);
							//收到微信的预付订单信息后需要重新生成签名
							String sign = getSign(respBean);
							respBean.setSign(sign);
							respBean.setOrderNumber(orderNum);
							hdDataPayDetailService.createPayDetail(payDetail);
							rest.setCode(ResultCode.SUCCESS.getCode());
							rest.setMsg("支付明细创建成功！");
							rest.setRst(respBean);
						}
					}
					
							
				}
			
			}
			
		}catch(Exception ex){
			this.logger.error("服务器出错！", ex);
			rest.setCode(ResultCode.ERROR.getCode());
			rest.setMsg("服务器出错！");
		}
		Object responseObject =  getResponseObject(jsonpCallback, rest);
		return responseObject;
	}
	
	/**
	 * 
	 * 查询订单支付明细
	 * @param jsonStr
	 * @param sessionId
	 * @param userAgent
	 * @param jsonpCallback
	 * @return
	 */
	@GET
	@Path("/order/single/query/{orderNumber}")
	@Produces(RestContents.MEDIA_TYPE_APPLICATION_JSON_UTF8)
	public Object querySinglePayDetail(@PathParam("orderNumber")String orderNumber,
			@CookieParam(HD_LOGIN_USER_COOKIE_KEY) String userCookieKey,
			@HeaderParam("User-Agent") String userAgent,
			@QueryParam("jsonpCallback")String jsonpCallback){
		RestObject<HdCommonPayDetailRo> rest = new RestObject<HdCommonPayDetailRo>();
		rest.setCode(ResultCode.VALID_NO_PASS.getCode());
		try{	
			boolean flag = true;
		
			if(null == orderNumber || "".equals(orderNumber)){
				flag = false;
				rest.setMsg("请传入订单号！");
			}
			
			if(flag){
				HdCommonPayDetailSo payDetailSo = new HdCommonPayDetailSo();
				payDetailSo.setOrderNumber(orderNumber);
				
				HdCommonPayDetailRo payDetailRo = hdDataPayDetailService.queryPayDetailBySo(payDetailSo);
				if(null != payDetailRo){
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setRst(payDetailRo);
					rest.setMsg("查询订单交易信息成功！");
				}else{
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setMsg("未查询到订单交易信息！");
				}
			}
			
		}catch(Exception ex){
			this.logger.error("服务器出错！", ex);
			rest.setCode(ResultCode.ERROR.getCode());
			rest.setMsg("服务器出错！");
		}
		Object responseObject =  getResponseObject(jsonpCallback, rest);
		return responseObject;
	}
	
	
	
	/**
	 * 
	 * 查询支付明细列表
	 * @param jsonStr
	 * @param sessionId
	 * @param userAgent
	 * @param jsonpCallback
	 * @return
	 */
	@GET
	@Path("/order/list/query/{minId}/{count}")
	@Produces(RestContents.MEDIA_TYPE_APPLICATION_JSON_UTF8)
	public Object queryPayDetailList(@PathParam("minId")Long minId,
			@PathParam("count") Integer count,
			@CookieParam(HD_LOGIN_USER_COOKIE_KEY) String userCookieKey,
			@HeaderParam("User-Agent") String userAgent,
			@QueryParam("jsonpCallback")String jsonpCallback){
		RestObject<HdCommonPayDetailRo> rest = new RestObject<HdCommonPayDetailRo>();
		rest.setCode(ResultCode.VALID_NO_PASS.getCode());
		try{	
			rest.setSingleFlag(false);
			rest.setCode(ResultCode.VALID_NO_PASS.getCode());
			boolean flag = true;
			if(null == count || "".equals(count)){
				flag = false;
				rest.setMsg("查询失败！数量不能为空！");
			}
			
				
			if(flag){
				if(null != minId){
					minId = minId == 0 ? HdCommonConstants.DEFAULT_MIN_ID :minId;
				}
			
				
				HdCommonPayDetailSo so = new HdCommonPayDetailSo();
				so.setUserCode(getLoginCode(userCookieKey));
				so.setMinId(minId);
				so.setPageSize(count);
				List<HdCommonPayDetailRo> roList = hdDataPayDetailService.queryPayDetailListBySo(so);
				if(null != roList && !roList.isEmpty()){
					for(HdCommonPayDetailRo payDetailRo : roList){
						String tradeCreateTimeStr = DateUtil.date2Str(payDetailRo.getTradeCreateTime(),"yyyy-MM-dd HH:mm:ss");
						String tradeExpireTimeStr = DateUtil.date2Str(payDetailRo.getTradeExpireTime(),"yyyy-MM-dd HH:mm:ss");
						
						//设置交易开始和失效时间的字符串
						payDetailRo.setTradeCreateTimeStr(tradeCreateTimeStr);
						payDetailRo.setTradeExpireTimeStr(tradeExpireTimeStr);
					}
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setRst(roList);
					rest.setMsg("查询支付记录列表成功！");
				}else{
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setMsg("您还没有支付记录！");
				}
					
				
				
			}
			
		}catch(Exception ex){
			this.logger.error("服务器出错！", ex);
			rest.setCode(ResultCode.ERROR.getCode());
			rest.setMsg("服务器出错！");
		}
		Object responseObject =  getResponseObject(jsonpCallback, rest);
		return responseObject;
	}
	
	/**
	 * 
	 * 查询支付明细列表
	 * @param jsonStr
	 * @param sessionId
	 * @param userAgent
	 * @param jsonpCallback
	 * @return
	 */
	@GET
	@Path("/pclist/query/{pageIndex}/{pageSize}")
	@Produces(RestContents.MEDIA_TYPE_APPLICATION_JSON_UTF8)
	public Object queryPcPayDetailList(@PathParam("pageIndex")Long pageIndex,
			@PathParam("pageSize")Integer pageSize,
			@CookieParam(HD_LOGIN_USER_COOKIE_KEY) String userCookieKey,
			@HeaderParam("User-Agent") String userAgent,
			@QueryParam("jsonpCallback")String jsonpCallback){
		RestObject<HdCommonPayDetailRo> rest = new RestObject<HdCommonPayDetailRo>();
		rest.setCode(ResultCode.VALID_NO_PASS.getCode());
		try{	
			rest.setSingleFlag(false);
			rest.setCode(ResultCode.VALID_NO_PASS.getCode());
			boolean flag = true;
			if(null == pageSize){
				flag = false;
				rest.setMsg("每页数据量不能为空");
			}
			
			if(null == pageIndex){
				flag = false;
				rest.setMsg("当前页码不能为空");
			}
			
				
			if(flag){
				
				HdCommonPayDetailSo so = new HdCommonPayDetailSo();
				so.setUserCode(getLoginCode(userCookieKey));
				so.setPageIndex(pageIndex);
				so.setPageSize(pageSize);
				List<HdCommonPayDetailRo> roList = hdDataPayDetailService.queryPayDetailListBySo(so);
				if(null != roList && !roList.isEmpty()){
					for(HdCommonPayDetailRo payDetailRo : roList){
						String tradeCreateTimeStr = DateUtil.date2Str(payDetailRo.getTradeCreateTime(),"yyyy-MM-dd HH:mm:ss");
						String tradeExpireTimeStr = DateUtil.date2Str(payDetailRo.getTradeExpireTime(),"yyyy-MM-dd HH:mm:ss");
						
						//设置交易开始和失效时间的字符串
						payDetailRo.setTradeCreateTimeStr(tradeCreateTimeStr);
						payDetailRo.setTradeExpireTimeStr(tradeExpireTimeStr);
					}
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setRst(roList);
					rest.setMsg("查询支付记录列表成功！");
				}else{
					rest.setCode(ResultCode.SUCCESS.getCode());
					rest.setMsg("您还没有支付记录！");
				}	
			}
			
		}catch(Exception ex){
			this.logger.error("服务器出错！", ex);
			rest.setCode(ResultCode.ERROR.getCode());
			rest.setMsg("服务器出错！");
		}
		Object responseObject =  getResponseObject(jsonpCallback, rest);
		return responseObject;
	}
	
	/**
	 * 
	 * 调用Alipay 支付Api
	 * @param payDetail
	 * @return
	 */
	private AlipayTradeAppPayResponse excuteAliPay(HdCommonPayDetail payDetail){
		
		String serverUrl = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_SERVER_URL);
		String appId = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_APP_ID);
		String appPrivateKey = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_APP_PRIVATE_KEY);
		String prodCode = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_PROD_CODE);
		String charset = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_CHARSET);
		String publicKey = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_PUBLIC_KEY);
		String encrypt = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_SIGN_TYPE);
		String notifyUrl = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_NOTIFY_URL);
		String timeout = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_TIMEOUT_EXPRESS);
		String appPayWay = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_ALIPAY_APP_QUICK_PAY_WAY);
		
		AlipayTradeAppPayResponse response = null;
		
		try{
			//实例化客户端
			AlipayClient alipayClient = new DefaultAlipayClient(serverUrl, appId, appPrivateKey, prodCode, charset, publicKey, encrypt);
			//实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
			AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
			//SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
			AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
			model.setBody(payDetail.getOrderDesc());
			model.setSubject(payDetail.getOrderSubject());
			model.setOutTradeNo(payDetail.getOrderNumber());
			model.setTimeoutExpress(timeout);
			model.setTotalAmount(payDetail.getAmount().toString());
			model.setProductCode(appPayWay);
			request.setBizModel(model);
			request.setNotifyUrl(notifyUrl);
			//这里和普通的接口调用不同，使用的是sdkExecute
	        response = alipayClient.sdkExecute(request);
	        
		}catch (Exception e) {
			logger.error(e.toString());
		}
		
		return response;
	}
	
	/**
	 * 
	 * 调用WechatPay 支付Api
	 * @param payDetail
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	private String excuteWechatPay(HdCommonPayDetail payDetail,HttpServletRequest request) throws Exception{
        
        String appid = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_APP_ID);
       // String appSecret = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_APP_SECRET);
        String notifyUrl = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_NOTIFY_URL);
       // String signType = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_SIGN_TYPE);
        String mchId = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_MCH_ID);
        String mchSecret = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_MCH_SECRET);
        //随机字符串
        String nonce_str = HdCommonWxPayUtil.generateNonceStr();
        //ip
        String ip = getIpAddress(request);
        
        String UTF8 = "UTF-8";
        String amount = String.valueOf(payDetail.getAmount().multiply(new BigDecimal("100")).intValue());
        
        //放入map
        Map<String, String> map = new HashMap<String, String>();
        map.put("appid", appid);
        map.put("body", payDetail.getOrderDesc());
        map.put("mch_id",mchId);
        map.put("nonce_str", nonce_str);
        map.put("sign_type", HdCommonWxPayConstants.HMACSHA256);
        map.put("notify_url", notifyUrl);
        map.put("out_trade_no", payDetail.getOrderNumber());
        map.put("spbill_create_ip", ip);
        map.put("total_fee", amount);
        map.put("trade_type", "APP");

        
        //生成xml格式的字符串
        String reqBodyXml = HdCommonWxPayUtil.generateSignedXml(map, mchSecret,SignType.HMACSHA256);
        int index = reqBodyXml.indexOf(">");
        String reqBodyXmlStr = reqBodyXml.substring(index+2);
        
        //调用Http请求 向WeChat 发起支付通知
        URL httpUrl = new URL("https://api.mch.weixin.qq.com/pay/unifiedorder");
        HttpURLConnection httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
        //httpURLConnection.setRequestProperty("Host", "api.mch.weixin.qq.com");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(10*1000);
        httpURLConnection.setReadTimeout(10*1000);
        httpURLConnection.connect();
        OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(reqBodyXmlStr.getBytes(UTF8));

        //获取内容
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
        final StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        String resp = stringBuffer.toString();
        if (stringBuffer!=null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream!=null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (outputStream!=null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return resp;
	}
	
	/**
	 * 
	   * 获得ip地址
	   * @param request
	   * @return
	 */
	private String getIpAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
				
		return ip;
	}
	
	/**
	 * 
	 * 生成预付单签名
	 * @param respBean
	 * @return
	 */
	private String getSign(HdDataWechatPayRespBean respBean){
		String sign = null;
		try{
			String appid = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_APP_ID);
			String mchSecret = configUtil.getConfigByKey(HdDataCommonConstant.HD_DATA_WECHAT_PAY_MCH_SECRET);
			
			Map<String, String> map = new HashMap<String, String>();
	        map.put("appid", appid);
	        map.put("partnerid", respBean.getMch_id());
	        map.put("prepayid",respBean.getPrepay_id());
	        map.put("package", "Sign=WXPay");
	        map.put("noncestr", respBean.getNonce_str());
	        map.put("timestamp", respBean.getTimestamp());
	        sign = HdCommonWxPayUtil.generateSignature(map, mchSecret, SignType.HMACSHA256);
		}catch (Exception e) {
			logger.error("生成预付单签名错误！",e);
		}
		return sign;
	}

}
