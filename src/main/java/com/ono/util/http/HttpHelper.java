package com.ono.util.http;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ono.enums.ContentTypes;
import com.ono.enums.Encodings;
import com.ono.enums.HttpMethod;
import com.ono.enums.StatusCode;
import com.ono.exceptions.InnerException;
import com.ono.util.common.RegexUtil;
import com.ono.util.common.StringUtil;
import com.ono.util.common.UrlUtil;
/**
 * Created by amosli on 13/07/2017.
 */
public abstract class HttpHelper {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

    public String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36";
    //public String userAgent = "Mozilla/5.0 (Linux; Android 5.2; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Mobile Safari/537.36";
    public Boolean circularRedirectsAllowed = false;
    /**
     * 是否自动跳转 302 301
     */
    public Boolean autoRedirect = true;
    /**
     * connectionRequestTimeout:从连接池中获取连接的超时时间，超过该时间未拿到可用连接，会抛出org.apache.http.conn.ConnectionPoolTimeoutException: Timeout waiting for connection from pool
     */
    public int connectionRequestTimeout = 20000;
    /**
     * connectTimeout:连接上服务器(握手成功)的时间，超出该时间抛出connect TIMEOUT
     * 默认20秒
     */
    public int connectTimeout = 20000;
    /**
     * socketTimeout:服务器返回数据(response)的时间，超过该时间抛出read TIMEOUT
     * 默认20秒
     */
    public int responseTimeout = 20000;
    public HttpConfig config = new HttpConfig();
    public String proxy;
    protected CookieStore cookies = new BasicCookieStore();
    protected CloseableHttpClient httpClient;

    public HttpHelper() {
        httpClient = getClient();
    }

    public static String getJSONParams(String param) {
        try {
            if (StringUtil.isEmpty(param)) {
                return "";
            }

            if (param.startsWith("{") && param.endsWith("}")) {
                return param;
            }

            JSONObject params = new JSONObject();
            for (String str : param.split("&")) {
                String name = str.substring(0, str.indexOf("="));
                String value = str.substring(str.indexOf("=") + 1);
                params.put(name, value);
            }
            return params.toString();
        } catch (JSONException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return "";
    }

    public static List<NameValuePair> getParams(String param, Encodings encodings) {
        if (StringUtil.isEmpty(param)) {
            return new ArrayList<>();
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String str : param.split("&")) {
            String name = UrlUtil.decodeIfEncoded(str.substring(0, str.indexOf("=")), encodings);
            String value = UrlUtil.decodeIfEncoded(str.substring(str.indexOf("=") + 1), encodings);
            params.add(new BasicNameValuePair(name, value));
        }
        return params;
    }

    public void addCookie(String name, String value, String domain) {
        BasicClientCookie basicClientCookie = new BasicClientCookie(name, value);
        basicClientCookie.setDomain(domain);
        addCookie(basicClientCookie);
    }

    public void addCookie(Cookie cookie) {
        cookies.addCookie(cookie);
    }


    public void addCookieString(String strCookies) {
        config.addHeader("cookie", strCookies);
    }

    public void updateCookieString() {
        List<Cookie> cookies = getCookies();
        if (cookies.size() < 1) {
            return;
        }

        Header[] headers = config.getHeaders();
        String cookieValue = "";
        for (Header header : headers) {
            if (header.getName().equalsIgnoreCase("cookie")) {
                cookieValue = header.getValue();
            }
        }

        for (Cookie c : cookies) {
            String name = c.getName();
            String value = c.getValue();
            if (StringUtil.isEmpty(cookieValue)) {
                cookieValue += name + "=" + value + "; ";
            } else {
                //a=b;c=d
                int i = cookieValue.indexOf(name);
                if (i < 0) {
                    cookieValue += "; " + name + "=" + value;
                } else {
                    String originCookie = RegexUtil.getValue(name + "=.*?;", cookieValue);
                    if (!StringUtil.isEmpty(originCookie)) {
                        cookieValue = cookieValue.replace(originCookie, name + "=" + value + ";");
                    }

                    String originCookieLast = RegexUtil.getValue(";\\s*" + name + "=.*?$", cookieValue);
                    if (!StringUtil.isEmpty(originCookieLast)) {
                        cookieValue = cookieValue.replace(originCookieLast, " ;" + name + "=" + value);
                    }
                }
            }
        }

        clearCookie();
        config.updateHeader("cookie", cookieValue);
    }

    public void addCookieString(String strCookies, String[] domains) {

        String[] cookies = strCookies.split(";");
        for (String cookie : cookies) {
            int index = cookie.indexOf("=");
            String cookieName = cookie.substring(0, index);
            String cookieValue = cookie.substring(index + 1);
            for (String domain : domains) {
                addCookie(cookieName, cookieValue, domain);
            }
        }
    }

    public void clearCookie() {
        cookies.clear();
    }

    public List<Cookie> getCookies() {
        return cookies.getCookies();
    }

    //**********get start **************//

    protected HttpHost getProxy() {
        return StringUtil.isEmpty(proxy) ? null : HttpHost.create(proxy);
    }

    public abstract CloseableHttpClient getClient();

    /**
     * 设置默认编码 utf8
     *
     * @param url
     * @return
     */
    public String goGet(String url) {
        return goGet(url, Encodings.UTF8);
    }

    public String goGetString(String url) {
        updateCookieString();
        return goGet(url, Encodings.UTF8);
    }

    public String goGet(String url, Encodings responseEncoding) {
        byte[] bytes = goGetBytes(url);
        if (StringUtil.isEmpty(bytes)) {
            return "timeout";
        }
        return new String(bytes, Charset.forName(responseEncoding.getValue()));
    }

    /**
     * 获取图片验证码 有可能用到
     *
     * @param url
     * @return
     */
    public byte[] goGetBytes(String url) {
        try {
            HttpResponse httpResponse = goGetResponse(url);
            if (StringUtil.isEmpty(httpResponse)) {
                return null;
            }
            return EntityUtils.toByteArray(httpResponse.getEntity());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    //**********get end **************//

    public HttpResponse goGetResponse(String url) {
        return request(url, HttpMethod.GET, null);
    }

    //**********post start **************//

    public String goPostString(String url,String params) {
        updateCookieString();
        return goPost(url, params, Encodings.UTF8, Encodings.UTF8);
    }

    public String goPost(String url) {
        return goPost(url, "", Encodings.UTF8, Encodings.UTF8);
    }

    public String goPost(String url, String params) {
        return goPost(url, params, Encodings.UTF8, Encodings.UTF8);
    }

    public String goPost(String url, String params, Encodings requestEncoding) {
        return goPost(url, params, requestEncoding, Encodings.UTF8);
    }

    public String goPost(String url, String params, Encodings requestEncoding, Encodings responseEncoding) {
        byte[] bytes = goPostBytes(url, params, requestEncoding);
        if (StringUtil.isEmpty(bytes)) {
            return "timeout";
        }
        return new String(bytes, Charset.forName(requestEncoding.getValue()));
    }

    public byte[] goPostBytes(String url, String params, Encodings requestEncoding) {
        HttpResponse post = goPostResponse(url, params, requestEncoding);
        if (StringUtil.isEmpty(post)) {
            return null;
        }

        byte[] bytes = new byte[0];
        try {
            bytes = EntityUtils.toByteArray(post.getEntity());
        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);
        }
        return bytes;
    }

    /**
     * 目前仅实现了 JSON和FORM形式
     *
     * @param url
     * @param params
     * @param requestEncoding
     * @return
     */
    public HttpResponse goPostResponse(String url, String params, Encodings requestEncoding) {

        ContentTypes contentType = config.getContentType();
        if (StringUtil.isEmpty(contentType)) {
            contentType = ContentTypes.FORM;
        }

        StringEntity httpEntity;
        switch (contentType) {
            case JSON:
                String jsonParams = getJSONParams(params);
                httpEntity = new StringEntity(jsonParams, Charset.forName(requestEncoding.getValue()));
                break;

            case FORM:
                List<NameValuePair> paramsList = getParams(params, requestEncoding);
                httpEntity = new UrlEncodedFormEntity(paramsList, Charset.forName(requestEncoding.getValue()));
                break;
            default:
                throw new InnerException(StatusCode.PARAMS_UNKNOWN);
        }

        httpEntity.setContentType(contentType.getValue());
        HttpResponse response = request(url, HttpMethod.POST, httpEntity);
        return response;
    }
    //**********post end **************//

    /**
     * 通用请求入口
     *
     * @param url
     * @param method
     * @param entity
     * @return
     */
    public HttpResponse request(String url, HttpMethod method, HttpEntity entity) {
        HttpEntityEnclosingRequestBase enclosingRequestBase = null;
        HttpRequestBase requestBase = null;
        switch (method) {
            case GET:
                requestBase = new HttpGet(url);
                break;
            case DELETE:
                requestBase = new HttpDelete(url);
                break;
            case HEAD:
                requestBase = new HttpHead(url);
                break;
            case OPTIONS:
                requestBase = new HttpOptions(url);
                break;
            case TRACE:
                requestBase = new HttpTrace(url);
                break;

            case POST:
                enclosingRequestBase = new HttpPost(url);
                break;
            case PUT:
                enclosingRequestBase = new HttpPut(url);
                break;
            case PATCH:
                enclosingRequestBase = new HttpPatch(url);
                break;
            default:
                throw new RuntimeException("unknown method:" + method);
        }

        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(getProxy()).setConnectTimeout(connectTimeout)
                .setSocketTimeout(responseTimeout)
                .setRedirectsEnabled(autoRedirect)
                .setCircularRedirectsAllowed(circularRedirectsAllowed)
                .setConnectionRequestTimeout(connectionRequestTimeout)
                .build();

        try {
            if (!StringUtil.isEmpty(requestBase)) {
                requestBase.setProtocolVersion(config.getHttpVersion());
                requestBase.setHeaders(config.getHeaders());
                requestBase.setConfig(requestConfig);
                return httpClient.execute(requestBase);

            } else {
                enclosingRequestBase.setProtocolVersion(config.getHttpVersion());
                enclosingRequestBase.setHeaders(config.getHeaders());
                enclosingRequestBase.setConfig(requestConfig);
                enclosingRequestBase.setEntity(entity);
                return httpClient.execute(enclosingRequestBase);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}
