package com.jufan.util;

import com.jufan.model.SSLResponse;
import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;

/**
 * @author 李尧
 * @since  0.2.0
 */
public class HttpUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);

    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT = 60000;

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        requestConfig = configBuilder.build();
    }

    public static String post(String url, String jsonStr) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        // 设置参数
        StringEntity s = new StringEntity(jsonStr, "UTF-8");
        s.setContentEncoding("UTF-8");
        s.setContentType("application/json");
        httpPost.setEntity(s);

        HttpResponse response = httpClient.execute(httpPost);

        if (response != null && response.getEntity() != null)
            return EntityUtils.toString(response.getEntity(), "UTF-8");

        return null;
    }

    public static SSLResponse sslPost(String url, String contentStr, Integer contentType) throws Exception {

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(createSSLConn())
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpPost post = new HttpPost(url);
        StringEntity entity = new StringEntity(contentStr, "UTF-8");
        entity.setContentEncoding("UTF-8");
        if (contentType == 1)
            entity.setContentType("application/x-www-form-urlencoded");
        if (contentType == 2)
            entity.setContentType("application/json");
        post.setEntity(entity);

        HttpResponse response = null;
        try {
            response = httpClient.execute(post);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new SSLResponse(parseRequest(post), response);
    }

    public static SSLResponse sslGet(String url) throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(createSSLConn())
                .setConnectionManager(connMgr)
                .setDefaultRequestConfig(requestConfig)
                .build();

        HttpGet get = new HttpGet(url);

        return new SSLResponse(get.toString(), httpClient.execute(get));
    }

    private static SSLConnectionSocketFactory createSSLConn() throws Exception {
        SSLConnectionSocketFactory sslsf;

        SSLContext context = new SSLContextBuilder()
                .loadTrustMaterial(null, (x509Certificates, s) -> false).build();

        sslsf = new SSLConnectionSocketFactory(context, (s, sslSession) -> true);

        return sslsf;
    }

    public static String parseHost(String url) {
        return url.split(":")[1].substring(2).split("/")[0];
    }

    public static Integer parsePort(String url) {

        String[] strs = url.split(":");
        if (strs.length > 2)
            return Integer.valueOf(url.split(":")[2].split("/")[0]);

        if ("HTTP".equals(strs[0].toUpperCase()))
            return 80;
        if ("HTTPS".equals(strs[0].toUpperCase()))
            return 443;

        return 0;
    }

    public static String parsePath(String url) throws Exception{
        int count = 0;

        char[] chars = url.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if ('/' == chars[i]) {
                count++;
                if (count == 3) {
                    return url.substring(i, url.length());
                }
            }
        }

        if (count < 3)
            return "/";

        throw new Exception("Parsing path error");
    }

    private static String parseRequest(HttpPost request) throws IOException {
        StringBuilder res = new StringBuilder()
                .append(request.toString()).append("\r\n");

        String entity = request.getEntity().toString();
        for (String s : entity.substring(1, entity.length() - 1).split(","))
            res.append(s).append("\r\n");

        res.append("\r\n").append(EntityUtils.toString(request.getEntity()));
        return res.toString();
    }

    public static String parseResponse(HttpResponse response) throws IOException {
        StringBuilder res = new StringBuilder()
                .append(response.getStatusLine()).append("\r\n");

        for (Header h : response.getAllHeaders())
            res.append(h.toString()).append("\r\n");

        res.append("\r\n").append(EntityUtils.toString(response.getEntity()));
        return res.toString();
    }

}
