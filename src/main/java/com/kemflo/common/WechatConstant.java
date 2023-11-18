package com.kemflo.common;

/**
 * @author Kevin
 */
public interface WechatConstant {
    String AUTHORIZATION = "Authorization";

    String GRANT_TYPE = "client_credentials";

    String ACCESS_TOKEN = "access_token";

    String TOKEN_TYPE = "Bearer";

    String ERR_CODE = "errcode";

    Integer ERR_CODE_SUCCESS = 0;

    String YST_MES = "yst-mes";

    String YST_MES_SCAN = "yst-mes-scan";

    String YST_MES_WS = "yst-mes-ws";

    String YST_MES_REPORT = "yst-mes-report";

    /**
     * MES token本地缓存刷新时间，单位秒
     */
    long MES_TOKEN_TTL = 7200;

    /**
     * 企业微信token本地缓存刷新时间，单位秒
     */
    long WECHAT_TOKEN_TTL = 5400;
}
