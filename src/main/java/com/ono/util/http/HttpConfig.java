package com.ono.util.http;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.message.BasicHeader;

import com.ono.enums.ContentTypes;
import com.ono.util.common.StringUtil;

/**
 * Created by amosli on 13/07/2017.
 */
public class HttpConfig {

    private String proxy;
    private ConcurrentSkipListSet<Header> headers = new ConcurrentSkipListSet<>((Header o1, Header o2) ->
            o1.getName().compareTo(o2.getName()));
    private ContentTypes contentType = ContentTypes.FORM;
    private HttpVersion httpVersion;
    private String referer;
    private String accept;

    public HttpConfig addHeader(String name, String value) {
        headers.add(new BasicHeader(name, value));
        return this;
    }
    
    public HttpConfig addHeader(Map<String, String> map) {
    	for (Map.Entry<String, String> entry : map.entrySet()) {
    		 headers.add(new BasicHeader(entry.getKey(), entry.getValue())); 
    		  }
        return this;
    }

    public HttpConfig updateHeader(String name, String value) {
        removeHeader(name);
        headers.add(new BasicHeader(name, value));
        return this;
    }

    public HttpConfig remvoeHeader(String name, String value) {
        headers.remove(new BasicHeader(name, value));
        return this;
    }

    public HttpConfig removeHeader(String name) {
        Header needRemovedHeader = null;
        for (Header header : headers) {
            if (header.getName().equals(name)) {
                needRemovedHeader = header;
            }
        }

        if (!StringUtil.isEmpty(needRemovedHeader)) {
            headers.remove(needRemovedHeader);
        }

        return this;
    }
    
    public ContentTypes getContentType() {
        return contentType;
    }

    public void setContentType(ContentTypes contentType) {
        this.contentType = contentType;
    }

    public Header[] getHeaders() {
        Header[] headerArray = new Header[headers.size()];
        return headers.toArray(headerArray);
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        addHeader("Referer", referer);
        this.referer = referer;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        addHeader("Accept", accept);
        this.accept = accept;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
    }
}
