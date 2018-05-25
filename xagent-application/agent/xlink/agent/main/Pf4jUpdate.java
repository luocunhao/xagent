package xlink.agent.main;

import java.util.List;

import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;

import com.github.zafarkhaja.semver.Version;

import ro.fortsoft.pf4j.update.PluginInfo;
import ro.fortsoft.pf4j.update.UpdateManager;
import ro.fortsoft.pf4j.update.UpdateRepository;
import xlink.mqtt.client.utils.LogHelper;

public class Pf4jUpdate {

  public static void main(String[] args) {
    try {
      PluginManager pluginManager = new DefaultPluginManager();
      pluginManager.loadPlugins();
      Pf4jUpdate.udpate(pluginManager);
    } catch (PluginException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  public static void udpate(PluginManager pluginManager) throws PluginException {
    // PluginManager pluginManager = XagentMain.pluginManger;

    Version systemVersion = Version.valueOf(pluginManager.getSystemVersion());
    UpdateManager updateManager = new UpdateManager(pluginManager);
    boolean systemUpToDate = true;
    List<UpdateRepository> repos = updateManager.getRepositories();

    if (updateManager.hasUpdates()) {
      List<PluginInfo> updates = updateManager.getUpdates();
      for (PluginInfo plugin : updates) {
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
          systemUpToDate = false;
        }

      }

    }

    if (updateManager.hasAvailablePlugins()) {
      List<PluginInfo> availablePlugins = updateManager.getAvailablePlugins();
      LogHelper.LOGGER().debug("Found %d available plugins", availablePlugins.size());
      for (PluginInfo plugin : availablePlugins) {
        LogHelper.LOGGER().debug("Found available plugin {}", plugin.id);
        PluginInfo.PluginRelease lastRelease = plugin.getLastRelease(systemVersion);
        String lastVersion = lastRelease.version;
        LogHelper.LOGGER().debug("Install plugin {} with version {}", plugin.id, lastVersion);
        /*
         * boolean installed = updateManager.installPlugin(plugin.id, lastVersion); if (installed) {
         * LogHelper.LOGGER().debug("Installed plugin {}", plugin.id); } else {
         * LogHelper.LOGGER().debug("Cannot install plugin {}", plugin.id); }
         */
      }
    }

    if (systemUpToDate) {
      LogHelper.LOGGER().debug("System up-to-date");
    }
  }

}
