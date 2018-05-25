package xlink.core.okhttp;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import xlink.core.okhttp.datastruct.OKHttpResponse;
import xlink.mqtt.client.utils.LogHelper;

public class OKHttp {

  private static final OKHttp singleton = new OKHttp();

  private static OkHttpClient client;

  private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  private OKHttp() {
    client = new OkHttpClient();
  }

  public static OKHttp instance() {
    return singleton;
  }

  public OKHttpResponse post(String url, String data) {
    Map<String,String> headers = new HashMap<String,String>();
    headers.put("Content-Type", "application/json");
    return post(url,data,headers,null);
  }

  public OKHttpResponse post(String url, String data, Map<String, String> headers,
      Map<String, String> querys) {
    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
    RequestBody body = RequestBody.create(JSON, data);
    Builder requestBuilder = new Request.Builder();
    // 构造头部
    if (headers != null && headers.isEmpty() == false) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.addHeader(header.getKey(), header.getValue());
      }
    }
    // 构造请求参数
    if (querys != null && querys.isEmpty() == false) {
      for (Map.Entry<String, String> query : querys.entrySet()) {
        urlBuilder.addQueryParameter(query.getKey(), query.getValue());
      }
    }

    Request request = requestBuilder.url(urlBuilder.build()).post(body).build();
    OKHttpResponse okHttpResponse = null;
    try {
      Response response = client.newCall(request).execute();
      int returnCode = response.code();
      String content = response.body().string();
      okHttpResponse = new OKHttpResponse(returnCode, content);
    } catch (Exception e) {
      LogHelper.LOGGER().error("okhttp execute failed.", e);
    }
    return okHttpResponse;
  }



  public OKHttpResponse get(String url, Map<String, String> headers, Map<String, String> querys) {
    HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
    Builder requestBuilder = new Request.Builder();
    // 构造头部
    if (headers != null && headers.isEmpty() == false) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        requestBuilder.addHeader(header.getKey(), header.getValue());
      }
    }
    // 构造请求参数
    if (querys != null && querys.isEmpty() == false) {
      for (Map.Entry<String, String> query : querys.entrySet()) {
        urlBuilder.addQueryParameter(query.getKey(), query.getValue());
      }
    }
    Request request = requestBuilder.url(urlBuilder.build()).build();
    OKHttpResponse okHttpResponse = null;
    try {
      Response response = client.newCall(request).execute();
      int returnCode = response.code();
      String content = response.body().string();
      okHttpResponse = new OKHttpResponse(returnCode, content);
    } catch (Exception e) {
      LogHelper.LOGGER().error("okhttp execute failed.", e);
    }
    return okHttpResponse;
  }


}
