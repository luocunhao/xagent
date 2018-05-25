package xlink.agent.pf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.pf4j.DefaultPluginRepository;

import xlink.mqtt.client.utils.LogHelper;

public class XlinkCmAgentPluginRepository extends DefaultPluginRepository {

  public XlinkCmAgentPluginRepository(Path pluginsRoot, boolean development) {
    super(pluginsRoot, development);
    // TODO Auto-generated constructor stub
  }

  @Override
  public boolean deletePluginPath(Path pluginPath) {
   File dir = pluginPath.toFile();
    // 删除相对应的zip文件
    String zipFile = pluginPath.toString() + ".zip";
    try {
      boolean delteFile = Files.deleteIfExists(Paths.get(zipFile));
      if (delteFile) {
        LogHelper.LOGGER().info("delete plugin zip file {} success.", zipFile);
      } else {
        LogHelper.LOGGER().info("delete plugin zip file {} failed.", zipFile);
      }
    } catch (IOException e) {
      LogHelper.LOGGER().error(e, "delete plugin zip file %s failed.", zipFile);
    }
    return deleteDir(dir);

  }

  public boolean deleteDir(File dir){
    if (dir.isDirectory()) {
      String[] children = dir.list();
      // 递归删除目录中的子目录下
      for (int i=0; i<children.length; i++) {
          boolean success = deleteDir(new File(dir, children[i]));
          if (!success) {
              return false;
          }
      }
  }
    // 目录此时为空，可以删除
    return dir.delete();
  }
}
