package xlink.xagent.ptp.huawei.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.conn.scheme.Scheme;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLSocketFactory;
import xlink.xagent.ptp.huawei.config.RequestMethod;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpsClient {
    public static HttpResponse access(String host, int port, String url, RequestMethod method
            , BasicNameValuePair[] headers, BasicNameValuePair[] parameters) throws Exception {
        List<BasicNameValuePair> headers2 = null;
        List<BasicNameValuePair> parameters2 = null;
        if (null != headers) {
            headers2 = new ArrayList<>();
            for (BasicNameValuePair e : headers) {
                headers2.add(e);
            }
        }
        if (null != null) {
            parameters2 = new ArrayList<>();
            for (BasicNameValuePair e : parameters) {
                parameters2.add(e);
            }
        }
        return access(host, port, url, method, headers2, parameters2);
    }

    public static HttpResponse access(String host, int port, String url,RequestMethod method,
                                      List<BasicNameValuePair> headers, List<BasicNameValuePair> parameters) throws Exception {
        //create a connection manager
        X509TrustManager tm = new X509TrustManager() {
            /**
             * 该方法检查客户端的证书，若不信任该证书则抛出异常。
             * 由于我们不需要对客户端进行认证，
             * 因此我们只需要执行默认的信任管理器的这个方法
             * */
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }
            /**
             * 该方法检查服务器的证书，若不信任该证书同样抛出异常。
             * 通过自己实现该方法，可以使之信任我们指定的任何证书。
             * 在实现该方法时，也可以简单的不做任何处理，即一个空的函数体，
             * 由于不会抛出异常，它就会信任任何证书。
             * */
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

            }
            //返回受信任的X509证书数组。
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        //create a SSL connection
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        /**KeyManager[] 第一个参数是授权的密钥管理器，用来授权验证。
         * TrustManager[]第二个是被授权的证书管理器，用来验证服务器端的证书。
         * 第三个参数是一个随机数值，可以填写null。
         * 如果只是服务器传输数据给客户端来验证，就传入第一个参数就可以，
         * 客户端构建环境就传入第二个参数。
         *双向认证的话，就同时使用两个管理器。
         */
        sslContext.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", port, socketFactory));

        //create a HttpClient to connect to the target host
        HttpClient httpClient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));

        //set the URL
        url = "https://" + host + ":" + port + url;
        System.out.println(url);

        //set the method
        HttpUriRequest httpRequest = null;
        if (RequestMethod.PUT.equals(method)) {
            HttpPut httpPut = new HttpPut(url);
            if (null != parameters) {
                UrlEncodedFormEntity tmp = new UrlEncodedFormEntity(parameters, "UTF-8");
                httpPut.setEntity(tmp);
            }
            httpRequest = httpPut;
        } else if (RequestMethod.POST.equals(method)) {
            HttpPost httpPost = new HttpPost(url);
            if (null != parameters) {
                UrlEncodedFormEntity tmp = new UrlEncodedFormEntity(parameters, "UTF-8");
                httpPost.setEntity(tmp);
            }
            httpRequest = httpPost;
        } else if (RequestMethod.GET.equals(method)) {
            if (null != parameters) {
                url += "?";
                boolean init = false;
                for (BasicNameValuePair e : parameters) {
                    if (!init) {
                        url += URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                        init = true;
                    } else {
                        url += "&" + URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                    }
                }
            }
            HttpGet httpGet = new HttpGet(url);
            httpRequest = httpGet;
        }else if(RequestMethod.DELETE.equals(method)){

            if (null != parameters) {
                url += "?";
                boolean init = false;
                for (BasicNameValuePair e : parameters) {
                    if (!init) {
                        url += URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                        init = true;
                    } else {
                        url += "&" + URLEncoder.encode(e.getName(), "UTF-8") + "=" + URLEncoder.encode(e.getValue(), "UTF-8");
                    }
                }
            }
            HttpDelete httpDelete = new HttpDelete(url);
            httpRequest = httpDelete;
        }else{
            return null;
        }

        //set headers
        if (null != headers) {
            for (BasicNameValuePair header : headers) {
                httpRequest.setHeader(header.getName(), header.getValue());
            }
        }

        //send the request
        HttpResponse response = httpClient.execute(httpRequest);
        return response;
    }

    /**
     * get the result
     */
    public static String getResult(HttpResponse response) throws IllegalStateException, IOException {
        InputStream is = response.getEntity().getContent();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String ret = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!ret.isEmpty()) {
                ret += "\n";
            }
            ret += line;
        }
        return ret;
    }
}
