package xlink.cm.agent.ptp;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import xlink.cm.agent.ptp.dataStruture.IPtpMessage;

/**
 * 自定义协议的编码器
 * 
 * @author xlink
 *
 */
public interface IPtpEncoder {

  /**
   * 把message转成二进制数据
   * 
   * @param byteBufAllocator ByteBuf的分配器，用于创建各种ByteBuf
   * @param message 自定义协议数据的对象
   * @param out 输出的对象
   * @exception 抛出错误
   * @return 二进制数据
   */
  public ByteBuf doEncode(ByteBufAllocator byteBufAllocator, IPtpMessage message, List<Object> out)
      throws Exception;
}
