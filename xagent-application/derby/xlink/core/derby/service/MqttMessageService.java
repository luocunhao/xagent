package xlink.core.derby.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.derby.dataStructure.DatapointMessageDerby;



public class MqttMessageService extends DerbyService {

  private static final Logger logger = LoggerFactory.getLogger(MqttMessageService.class);

  private static final MqttMessageService singleton = new MqttMessageService();

  private MqttMessageService() {

  }

  public static MqttMessageService instance() {
    return singleton;
  }

  /**
   * 插入消息
   * 
   * @param deviceId
   * @param topic
   * @param createTime
   * @param data
   * @param isSended
   * 
   * @return 新增记录的id
   */
  public long insert(String topic, byte[] data, int qos, Date createTime, boolean isSended)
      throws SQLException {
    String insertSql =
        "INSERT INTO MQTT_MESSAGE(topic,payload,qos,update_time,is_sended) VALUES(?,?,?,?,?) ";
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(insertSql);
      pst.setString(1, topic);
      pst.setBytes(2, data);
      pst.setInt(3, qos);
      pst.setDate(4, createTime);


      pst.setShort(5, (short) (isSended ? 1 : 0));
      pst.executeUpdate();

    } catch (SQLException e) {
      logger.error("", e);
      throw e;
    } finally {
      release(mConnection, pst, rs);
    }
    return 0;
  }

  /**
   * 查找没有发送的消息
   * 
   * @return
   * @throws SQLException
   */
  public List<DatapointMessageDerby> findUnsendMessage(int offset, int limit) {
    List<DatapointMessageDerby> result = new ArrayList<DatapointMessageDerby>();
    String querySql = "SELECT * FROM MQTT_MESSAGE WHERE is_sended=? OFFSET " + offset
        + " ROWS FETCH NEXT " + limit + " ROWS ONLY";
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(querySql);
      pst.setShort(1, (short) 0);
      rs = pst.executeQuery();
      while (rs.next()) {
        int id = rs.getInt(1);
        int deviceId = rs.getInt(2);
        String topic = rs.getString(3);
        Date updateTime = rs.getDate(4);
        byte[] data = rs.getBytes(5);
        boolean sendStatus = rs.getBoolean(6);
        result.add(new DatapointMessageDerby(id, deviceId, topic, data, updateTime, sendStatus));
      }
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, rs);
    }

    return result;
  }

  /**
   * 删除已经发送成功的消息
   */
  public void deleteSendedMessage() {
    String insertSql = "DELETE FROM MQTT_MESSAGE WHERE is_sended=?";
    PreparedStatement pst = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(insertSql);
      pst.setShort(1, (short) 1);
      pst.executeUpdate();
    } catch (SQLException e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, null);
    }
  }

  public void updateMessageToSended(int messageId) {
    String querySql = "UPDATE MQTT_MESSAGE SET is_sended=? WHERE id=?";
    PreparedStatement pst = null;
    ResultSet rs = null;
    Connection mConnection = null;
    try {
      mConnection = getConnection();
      pst = mConnection.prepareStatement(querySql);
      pst.setShort(1, (short) 1);
      pst.setInt(2, messageId);
      pst.executeUpdate();
    } catch (Exception e) {
      logger.error("", e);
    } finally {
      release(mConnection, pst, rs);
    }
  }
}
