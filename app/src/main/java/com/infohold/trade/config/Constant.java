/*
 * Created by zhangchong on 4/7/2016.
 * Copyright (c) 2016 com.infohold.TradeInfoSys. All rights reserved.
 */

package com.infohold.trade.config;

public class Constant {

    /**
     * 服务器地址
     */
//    public static final String ServiceAddress = "http://192.168.0.198:80";
    public static final String ServiceAddress = "http://192.168.1.112:80";

    /**
     * 图片地址前缀
     */
    public static final String ServicePicAddress = ServiceAddress + "/OA/";

    /**
     * 服务URL
     */
    public static final String ServiceEndpoint = ServiceAddress + "/OA/index.php/APPApi/";

    /**
     * 活动详情
     */
    public static final String ServiceBulletinDetail = ServiceAddress + "/oa/APPApi/detailsActivityInfo?id=";

    /**
     * 调用服务返回状态码
     */
    public static final String RepCode = "repCode";

    /**
     * 服务成功返回状态码
     */
    public static final String RepCodeSuccess = "0000";

    /**
     * 调用服务返回信息
     */
    public static final String RepMSG = "repMSG";


    /**
     * 调用服务返回数据
     */
    public static final String RepData = "repData";


    /**
     *  Intent传递数据
     */
    public static final String PutExtraDataStr = "data";

    /**
     * 登录人员信息
     */
    public static final String USER_INFO = "login_user";
    public static final String USER_INFO_ID = "storeId";
    public static final String USER_INFO_NAME = "storeName";
    public static final String USER_INFO_GRADE = "memberGrade";

    /**
     * 请求相机
     */
    public static final int REQUEST_CODE_ASK_PERMISSIONS_FROM_MAIN = 1;
    public static final int REQUEST_CODE_ASK_PERMISSIONS_FROM_ADAPTER = 2;

    /**
     * Handler消息what
     */
    public static final String HAS_CONTENT = "has";
    public static final String HAS_EMPTY_CONTENT = "";

    /**
     * 扫描二维码前缀
     */
    public static final String SCAN_BAR_CODE_PREFIX = "memberCard=";

    /**
     * admin权限判断
     */

    public static final String ADMIN = "1";

    /**
     * 下拉刷新个数
     */
    public static final int PageSize = 10;
}



