package org.gateway.pf4j.application;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import xlink.core.derby.DerbyTableTools;
import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.derby.service.DeviceInfoService;

public class DerbyDBDeviceServiceTest {

  @Test
  public void findAllDeviceNotFound() {
    // 初始化derby
    try {
      DerbyTableTools.instance().init();
      List<DeviceInfo> list = DeviceInfoService.instance().findAllOnline("karcher_plugin", 0, 20);
      System.out.println(list);
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
