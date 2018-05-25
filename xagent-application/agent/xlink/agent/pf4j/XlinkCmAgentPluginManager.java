package xlink.agent.pf4j;

import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.ExtensionFactory;
import org.pf4j.PluginState;
import org.pf4j.PluginWrapper;

import xlink.cm.ptp.platform.PtpFramework;
import xlink.cm.ptp.server.DefaultPtpServer;
import xlink.mqtt.client.MqttClientManager;
import xlink.mqtt.client.utils.LogHelper;


public class XlinkCmAgentPluginManager extends DefaultPluginManager {



  @Override
  protected ExtensionFactory createExtensionFactory() {
    return new XlinCmAgentExtensionFactory();
  }

  @Override
  public PluginState stopPlugin(String pluginId) {
    if (PluginState.STOPPED == this.getPlugin(pluginId).getPluginState()) {
      LogHelper.LOGGER().info("plugin {} is already stopped.", pluginId);
      return PluginState.STOPPED;
    }
    PluginState pluginState = super.stopPlugin(pluginId);
    DefaultPtpServer server = PtpFramework.instance().destroyPtpServer(pluginId);
    if (server != null) {
      MqttClientManager.instance().deleteMqttClient(server.getXlinkCertId());
    }
    // 卸载插件
    boolean result = this.unloadPlugin(pluginId);
    LogHelper.LOGGER().info("unplaod ptp {}, result is: {}", pluginId, result);
    return pluginState;
  }

  @Override
  public void startPlugins() {
    List<PluginWrapper> wappers = this.getPlugins();
    for (final PluginWrapper wapper : wappers) {
      new Thread(new Runnable() {
        @Override
        public void run() {
          XlinkCmAgentPluginManager.this.startPlugin(wapper.getPluginId());
        }
      }).start();

    }
  }

}
