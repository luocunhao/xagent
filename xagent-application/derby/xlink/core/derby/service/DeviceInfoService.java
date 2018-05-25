package xlink.core.derby.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.derby.dataStructure.DeviceInfo;
import xlink.core.utils.ContainerGetter;

public class DeviceInfoService extends DerbyService {

  private static final Logger logger = LoggerFactory.getLogger(DeviceInfoService.class);

  private static final String TABLE_PREFIX = "DEVICE_INFO_";

  private static String INSERT_SQL =
      "INSERT INTO DEVICE_INFO_{PTP_ID}(device_id,device_key,product_id,corp_id,mac,is_online,state_update_time) VALUES(?,?,?,?,?,?,?)";
  
  private static String DEVICEID_QUERY_SQL = "SELECT * FROM DEVICE_INFO_{PTP_ID} WHERE device_id=?";
  
  private static String PIDMAC_QUERY_SQL = "SELECT * FROM DEVICE_INFO_{PTP_ID} WHERE product_id=? and mac=?";
  
  private static String DEVICE_UPDATE_SQL =
      "UPDATE DEVICE_INFO_{PTP_ID} SET device_key = ?,product_id = ?,corp_id = ?,mac = ?,is_online = ?,state_update_time=? WHERE  device_id=?";
  
  private static String ONLINE_UPDATE_SQL =
      "UPDATE DEVICE_INFO_{PTP_ID} SET is_online = ?,state_update_time=? WHERE  device_id=?";

  private static String ALL_ONLINE_DEVIE_QUERY_SQL =
      "SELECT * FROM DEVICE_INFO_{PTP_ID} WHERE is_online=? OFFSET {offset} ROWS FETCH NEXT {limit} ROWS ONLY";
  
  private static String DELETE_SQL = "DELETE FROM DEVICE_INFO_{PTP_ID} WHERE product_id=? and mac=?";

  private static final DeviceInfoService singleton = new DeviceInfoService();

  private DeviceInfoService() {

  }

  public static DeviceInfoService instance() {
    return singleton;
  }


  public DeviceInfo findDeviceInfo(String ptpId, int xlinkDeviceId) {
    String sql = DEVICEID_QUERY_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(sql);
      pst.setInt(1, xlinkDeviceId);
      rs = pst.executeQuery();
      while (rs.next()) {
        return fromResultSet(rs);
      }
    } catch (SQLException e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, rs);
    }
    return null;
  }

  public void insertBatch(String ptpId,List<DeviceInfo> deviceKeys) throws Exception {
    String insertSql = INSERT_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
    PreparedStatement pst = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(insertSql);
      for (DeviceInfo deviceKey : deviceKeys) {
        pst.setInt(1, deviceKey.getDeviceId());
        pst.setString(2, deviceKey.getDeviceKey());
        pst.setString(3, deviceKey.getProductId());
        pst.setString(4, deviceKey.getCorpId());
        pst.setString(5, deviceKey.getMac());
        pst.setBoolean(6, deviceKey.isOnline());
        pst.setTimestamp(7, new Timestamp(deviceKey.getStateUpdateTime().getTime()));
        pst.executeUpdate();
      }
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
  }

  public void insert(String ptpId,DeviceInfo deviceKey) {
    String insertSql = INSERT_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
    PreparedStatement pst = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(insertSql);
      pst.setInt(1, deviceKey.getDeviceId());
      pst.setString(2, deviceKey.getDeviceKey());
      pst.setString(3, deviceKey.getProductId());
      pst.setString(4, deviceKey.getCorpId());
      pst.setString(5, deviceKey.getMac());
      pst.setBoolean(6, deviceKey.isOnline());
      pst.setTimestamp(7, new Timestamp(deviceKey.getStateUpdateTime().getTime()));
      pst.executeUpdate();
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
  }

  public void updateDeviceInfo(String ptpId, DeviceInfo deviceInfo) {
    String updateSql = DEVICE_UPDATE_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
    Connection mConnection = null;
    PreparedStatement pst = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(updateSql);
      pst.setString(1, deviceInfo.getDeviceKey());
      pst.setString(2, deviceInfo.getProductId());
      pst.setString(3, deviceInfo.getCorpId());
      pst.setString(4, deviceInfo.getMac());
      pst.setBoolean(5, deviceInfo.isOnline());
      pst.setTimestamp(6, new Timestamp(deviceInfo.getStateUpdateTime().getTime()));
      pst.setInt(7, deviceInfo.getDeviceId());
      pst.executeUpdate();
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
  }

  public void updateDeviceState(String ptpId, int deviceId, boolean isOnline,
      Date stateUpdateTime) {
    String updateSql = ONLINE_UPDATE_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
 ;
    Connection mConnection = null;
    PreparedStatement pst = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(updateSql);
      pst.setBoolean(1, isOnline);
      pst.setTimestamp(2, new Timestamp(stateUpdateTime.getTime()));
      pst.setInt(3, deviceId);
      pst.executeUpdate();
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
  }

  public DeviceInfo findByProductIdAndMac(String ptpId, String productId, String mac) {
    String querySql = PIDMAC_QUERY_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(querySql);
      pst.setString(1, productId);
      pst.setString(2, mac);
      rs = pst.executeQuery();
      while (rs.next()) {
        return fromResultSet(rs);
      }
    } catch (SQLException e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, rs);
    }
    return null;
  }

  public List<DeviceInfo> findAllOnline(String ptpId, int offset, int limit) {
    String querySql = ALL_ONLINE_DEVIE_QUERY_SQL.replaceAll("\\{PTP_ID\\}", ptpId)
        .replaceAll("\\{offset\\}", Integer.toString(offset))
        .replaceAll("\\{limit\\}", Integer.toString(limit));
    Connection mConnection = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    List<DeviceInfo> deviceList = ContainerGetter.arrayList();
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(querySql);
      pst.setBoolean(1, true);
      rs = pst.executeQuery();
      while (rs.next()) {
        deviceList.add(fromResultSet(rs));
      }
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
    return deviceList;
  }
  
  public boolean deleteDeviceInfo(String ptpId,String productId,String mac) {
	  String delteSql = DELETE_SQL.replaceAll("\\{PTP_ID\\}", ptpId);
	  Connection mConnection = null;
	    PreparedStatement pst = null;
	    try {
	      mConnection = getConnection();
	      pst = mConnection.prepareStatement(delteSql);
	      pst.setString(1, productId);
	      pst.setString(2, mac);
	      int result = pst.executeUpdate();
	      return result>0;
	    } catch (Exception e) {
	      logger.error("", e);
	      return false;
	    } finally {
	      release(mConnection, pst, null);
	    }
  }

  private DeviceInfo fromResultSet(ResultSet rs) throws SQLException {
    int deviceId = rs.getInt(1);
    String deviceKey = rs.getString(2);
    String productId = rs.getString(3);
    String corpId = rs.getString(4);
    String mac = rs.getString(5);
    boolean isOnline = rs.getBoolean(6);
    Date stateUpdateTime = new Date(rs.getTimestamp(7).getTime());
    return new DeviceInfo(deviceId, productId, mac, corpId, deviceKey, isOnline, stateUpdateTime);
  }

  private String ResultSetToString(ResultSet rs) throws SQLException {
    StringBuilder builder = new StringBuilder();
    builder.append(rs.getInt(1)).append(" , ").append(rs.getString(2)).append(" , ")
        .append(rs.getString(3)).append(" , ").append(rs.getString(4)).append(" , ")
        .append(rs.getString(5)).append(" , ").append(rs.getBoolean(6)).append(" , ")
        .append(rs.getTimestamp(7).getTime()).append("\n");
    return builder.toString();
    
  }
}
