package xlink.agent.pf4j;

import org.pf4j.ExtensionFactory;

import xlink.cm.agent.extensions.XagentApi;
import xlink.cmagent.extention.XagentImpl;
import xlink.mqtt.client.utils.LogHelper;

public class XlinCmAgentExtensionFactory implements ExtensionFactory {

  @Override
  public Object create(Class<?> extensionClass) {
    LogHelper.LOGGER().debug("Create instance for extension {}", extensionClass.getName());
    try {

      if (XagentApi.class.isAssignableFrom(extensionClass)) {
        return new XagentImpl();
      } else {
        return extensionClass.newInstance();
      }


    } catch (Exception e) {
      LogHelper.LOGGER().error(e.getMessage(), e);
    }

    return null;
  }

}
