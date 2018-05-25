package xlink.agent.shell.command;

import java.io.File;
import java.nio.file.Path;

import org.pf4j.PluginState;
import org.pf4j.util.ZipFileFilter;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import xlink.agent.main.XagentMain;

public class InstallPtpCommand implements Command {
  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "intall ptp";
  }

  @Override
  public String getUsage() {
    // TODO Auto-generated method stub
    return "installPtp <fileName>";
  }

  @Override
  public CommandType getCommandType() {
    // TODO Auto-generated method stub
    return CommandType.InstallPlugin;
  }


  @Override
  public void execute(Channel channel, String... args) {

    if (args == null || args.length < 1) {
      String notPluginId = "please input fileName...";
      channel.write(Unpooled.wrappedBuffer(notPluginId.getBytes()));
      return;
    }
    String fileName = args[0];
    try {
      Path pluginPath = getTargeZipFile(fileName);
      String pluginId = XagentMain.pluginManger.loadPlugin(pluginPath);
      PluginState state = XagentMain.pluginManger.startPlugin(pluginId);
      String reply = "start ptp execute successfully, ptp state is: " + state;
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    } catch (Exception e) {
      String reply = "start ptp failed, cause by " + e.getMessage();
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    }


  }

  private Path getTargeZipFile(String fileName) throws Exception {
    File[] files = XagentMain.pluginManger.getPluginsRoot().toFile().listFiles(new ZipFileFilter());
    for (File file : files) {

      if (file.getName().contains(fileName)) {
        return file.toPath();
      }
    }
    throw new Exception("can not find " + fileName + " in plugins folder.");
  }

}
