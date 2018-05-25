package xlink.core.utils;

import io.netty.channel.Channel;

public class PtpUtil {
  public static final int getSocketHash(Channel channel) {
    return 0x7fffffff & channel.hashCode();
  }
}
