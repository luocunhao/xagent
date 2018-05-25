package xlink.agent.shell.command;

import org.pf4j.PluginState;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import xlink.agent.main.XagentMain;

public class StartPtpCommand implements Command {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "start ptp";
  }

  @Override
  public String getUsage() {
    // TODO Auto-generated method stub
    return "startPtp <pluginId>";
  }

  @Override
  public CommandType getCommandType() {
    // TODO Auto-generated method stub
    return CommandType.StartPlugin;
  }


  @Override
  public void execute(Channel channel, String... args) {

    if (args == null || args.length < 1) {
      String notPluginId = "please input fileName...";
      channel.write(Unpooled.wrappedBuffer(notPluginId.getBytes()));
      return;
    }
    String pluginId = args[0];
    try {
      PluginState state = XagentMain.pluginManger.startPlugin(pluginId);
      String reply = "start ptp execute successfully, ptp state is: " + state;
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    } catch (Exception e) {
      String reply = "start ptp failed, cause by " + e.getMessage();
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    }


  }

}

