package com.ono.util.common;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.ono.enums.Encodings;

/**
 * Created by amosli on 08/02/2017.
 */
public class UrlUtil {

    /**
     * !	#	$	&	'	(	)	*	+	,	/	:	;	=	?	@	[	]
     * 百分号保留字
     */
    private static final String[] PERCENT_ENCODING_RESERVED_CHARS = new String[]{"%21", "%23", "%24", "%26", "%27", "%28", "%29", "%2A", "%2B", "%2C", "%2F", "%3A", "%3B", "%3D", "%3F", "%40", "%5B", "%5D"};
    private static final String URL_ENCODED = "%[0-9a-fA-F]{2}";

    public static String encode(String url) {
        try {
            return URLEncoder.encode(url, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static Boolean isUrlEncoded(String url) {
        return RegexUtil.isMatch(URL_ENCODED, url);
    }

    public static String decode(String url, Encodings encodings) {
        try {
            return URLDecoder.decode(url, encodings.getValue());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * 如果已经编码,则解码;否则,返回原字符串
     *
     * @param url
     * @param encodings
     * @return
     */
    public static String decodeIfEncoded(String url, Encodings encodings) {
        if (!isUrlEncoded(url)) {
            return url;
        }
        return decode(url, encodings);

    }

    public static String trimScheme(String url) {
        return url.replace("https://", "").replace("http://", "");
    }

}
