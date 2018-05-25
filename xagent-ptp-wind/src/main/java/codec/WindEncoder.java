package codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import xlink.cm.agent.ptp.IPtpEncoder;
import xlink.cm.agent.ptp.dataStruture.IPtpMessage;

import java.util.List;

public class WindEncoder implements IPtpEncoder {
    @Override
    public ByteBuf doEncode(ByteBufAllocator byteBufAllocator, IPtpMessage iPtpMessage, List<Object> list) throws Exception {
        return null;
    }
}
