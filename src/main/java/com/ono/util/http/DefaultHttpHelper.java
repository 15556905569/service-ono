package com.ono.util.http;
//jdk

import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

/**
 * Created by amosli on 13/07/2017.
 */
public class DefaultHttpHelper extends HttpHelper {

    /**
     * 跳过证书验证
     *
     * @return
     */
    @Override
    public CloseableHttpClient getClient() {
        try {

            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                        throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            };

            //获取TLS安全协议上下文
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new TrustManager[]{xtm}, null);
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);

            RequestConfig defaultConfig = RequestConfig.custom()
                    .setCookieSpec(CookieSpecs.NETSCAPE)
                    .setExpectContinueEnabled(true)
                    .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                    .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(responseTimeout)
                    .setCircularRedirectsAllowed(circularRedirectsAllowed)
                    .setRedirectsEnabled(autoRedirect)
                    .build();
            Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", sslConnectionSocketFactory).build();
            PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager(sfr);

            return HttpClients.custom().setConnectionManager(pcm)
                    .setDefaultRequestConfig(defaultConfig)
                    .setDefaultCookieStore(cookies)
                    .setUserAgent(userAgent)
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setProxy(getProxy())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return HttpClients.custom().build();
    }
}
