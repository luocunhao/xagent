package xlink.agent.shell.command;

import org.pf4j.PluginState;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import xlink.agent.main.XagentMain;

public class StopPtpCommand implements Command {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUsage() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public CommandType getCommandType() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void execute(Channel channel, String... args) {
    if (args == null || args.length < 1) {
      String notPluginId = "please input ptpId...";
      channel.write(Unpooled.wrappedBuffer(notPluginId.getBytes()));
      return;
    }
    String pluginId = args[0];
    try {
      PluginState state = XagentMain.pluginManger.stopPlugin(pluginId);
      String reply = "stop ptp execute successfully, ptp state is: " + state;
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    } catch (Exception e) {
      String reply = "stop ptp failed, cause by " + e.getMessage();
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    }

  }

}
