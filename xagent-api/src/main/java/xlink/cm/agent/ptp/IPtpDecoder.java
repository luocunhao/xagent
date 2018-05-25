package xlink.cm.agent.ptp;

import java.util.List;

import io.netty.buffer.ByteBuf;
import xlink.cm.agent.ptp.exception.PtpException;

/**
 * 自定义协议的解码器
 * 
 * @author xlink
 */
public interface IPtpDecoder {

  /**
   * 解析二进制数据，并最终转成{@link xlink.cm.agent.ptp.dataStruture.IPtpMessage}类型的对象
   * 
   * @param in 输入二进制数据
   * @param out 包含{@link xlink.cm.agent.ptp.dataStruture.IPtpMessage}的对象
   */
  public void doDecode(ByteBuf in, List<Object> out) throws PtpException;
}
