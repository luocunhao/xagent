package xlink.agent.shell.command;

import java.util.List;

import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import com.github.zafarkhaja.semver.Version;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import ro.fortsoft.pf4j.update.PluginInfo;
import ro.fortsoft.pf4j.update.UpdateManager;
import xlink.agent.main.XagentMain;
import xlink.mqtt.client.utils.LogHelper;

public class UpdatePtpCommand implements Command {

  private UpdateManager updateManager = null;

  public UpdatePtpCommand() {
    updateManager = new UpdateManager(XagentMain.pluginManger);
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "update ptp";
  }

  @Override
  public String getUsage() {
    // TODO Auto-generated method stub
    return "update ptp <pluginId> [local | remote]";
  }

  @Override
  public CommandType getCommandType() {
    // TODO Auto-generated method stub
    return CommandType.UpdatePlugin;
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
      String result = udpate(XagentMain.pluginManger, pluginId);
      channel.write(Unpooled.wrappedBuffer(result.getBytes()));
    } catch (Exception e) {
      String reply = "update ptp failed, cause by " + e.getMessage();
      channel.write(Unpooled.wrappedBuffer(reply.getBytes()));
    }

  }


  public String udpate(PluginManager pluginManager, String pluginId) throws PluginException {
    // PluginManager pluginManager = XagentMain.pluginManger;
    Version systemVersion = Version.valueOf(pluginManager.getSystemVersion());

    if (updateManager.hasUpdates()) {
      List<PluginInfo> updates = updateManager.getUpdates();
      for (PluginInfo plugin : updates) {
        if (plugin.id.equals(pluginId)) {
          LogHelper.LOGGER().debug("Found update for plugin {}", plugin.id);
          PluginInfo.PluginRelease lastRelease = plugin.getLastRelease(systemVersion);
          String lastVersion = lastRelease.version;
          String installVersion =
              pluginManager.getPlugin(plugin.id).getDescriptor().getVersion().toString();
          LogHelper.LOGGER().debug("Update plugin {} from version {} to version {}", plugin.id,
              installVersion, lastVersion);

          boolean update = updateManager.updatePlugin(plugin.id, lastVersion);

          if (update) {
            LogHelper.LOGGER().debug("Update plugin {}", plugin.id);
          } else {
            LogHelper.LOGGER().debug("Cannot update plugin {}", plugin.id);
          }
          return "update ptp successfully.";
        }

      }

    }
    return "can not find update ptp.";
  }

}
