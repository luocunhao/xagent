package xlink.core.derby;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.derby.service.DerbyService;
import xlink.mqtt.client.utils.LogHelper;

public class DerbyTableTools extends DerbyService {

  private static final Logger logger = LoggerFactory.getLogger(DerbyTableTools.class);

  private static final DerbyTableTools singleton = new DerbyTableTools();


  private DerbyTableTools() {}

  public static DerbyTableTools instance() {
    return singleton;
  }

  public void init() throws SQLException {
    if (checkTableExist("MQTT_MESSAGE") == false) {
      createMqttMessageTable();
    }
  }

  public void createDeviceInfoIfAbsent(String ptpId) {
    try {
      String talbeName = "DEVICE_INFO_" + ptpId;
      if (checkTableExist(talbeName) == false) {
        createDeviceInfoTable(ptpId);
      }
    } catch (Exception e) {
      LogHelper.LOGGER().error("create device info ifAbsent failed", e);
    }

  }

  private void createMqttMessageTable() throws SQLException {
    StringBuilder tableSqlBuilder = new StringBuilder();
    tableSqlBuilder.append("CREATE TABLE MQTT_MESSAGE(").append("\n")
        .append("id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),")
        .append("\n").append("topic VARCHAR(100),").append("\n").append("payload BLOB(1024),")
        .append("\n").append("qos INTEGER,").append("\n").append("update_time DATE,").append("\n")
        .append("is_sended SMALLINT").append("\n").append(")");
    logger.info("\n======================== create talbe =============================\n"
        + tableSqlBuilder.toString() + "\n"
        + "==============================================================");

    getConnection().createStatement().execute(tableSqlBuilder.toString());

  }

  private void createDeviceInfoTable(String ptpId) throws SQLException {
    StringBuilder tableSqlBuilder = new StringBuilder();
    tableSqlBuilder.append("CREATE TABLE DEVICE_INFO_").append(ptpId).append("(\n")
        .append("device_id INTEGER NOT NULL,").append("\n").append("device_key VARCHAR(100),")
        .append("\n").append("product_id VARCHAR(60),").append("\n").append("corp_id VARCHAR(30),")
        .append("\n").append("mac VARCHAR(65),").append("\n").append("is_online SMALLINT,")
        .append("\n").append("state_update_time TIMESTAMP,").append("\n")
        .append("PRIMARY KEY (device_id)").append("\n").append(")");
    logger.info("\n======================== create talbe =============================\n"
        + tableSqlBuilder.toString() + "\n"
        + "==============================================================");

    getConnection().createStatement().execute(tableSqlBuilder.toString());

  }


  private boolean checkTableExist(String tableName) throws SQLException {
    String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
    try {
      ResultSet rs = getConnection().createStatement().executeQuery(sql);
      if (rs == null)
        return false;
      else
        return true;
    } catch (Exception e) {
      return false;
    }
  }

  public void dropTable(String tableName) throws SQLException {
    String sql = String.format("DROP TABLE %s", tableName);
    try {
      getConnection().createStatement().execute(sql);

    } catch (Exception e) {
      logger.error("", e);
    }
  }
}
