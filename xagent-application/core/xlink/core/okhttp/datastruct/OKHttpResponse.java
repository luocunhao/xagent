package xlink.core.okhttp.datastruct;

public class OKHttpResponse {

  /**
   * http返回码
   */
  private int httpReturnCode;
  /**
   * http返回的内容
   */
  private String content;

  public OKHttpResponse(int httpReturnCode, String content) {
    this.httpReturnCode = httpReturnCode;
    this.content = content;
  }

  public int getHttpReturnCode() {
    return httpReturnCode;
  }

  public String getContent() {
    return content;
  }

  @Override
  public String toString() {
    return "OKHttpResponse [httpReturnCode=" + httpReturnCode + ", content=" + content + "]";
  }



}
