package com.ono.util.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;

/**
 * Created by amosli on 20/07/2017.
 */
public class SimpleHttpHelper extends HttpHelper {
    @Override
    public CloseableHttpClient getClient() {
        return HttpClients.custom()
                .setDefaultCookieStore(cookies)
                .setUserAgent(userAgent)
                .setRedirectStrategy(new LaxRedirectStrategy())
                .build();
    }
}
