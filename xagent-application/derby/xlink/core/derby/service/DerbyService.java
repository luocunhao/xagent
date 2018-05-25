package xlink.core.derby.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xlink.core.derby.DerbyLocalPoolManager;

public abstract class DerbyService {

  private static final Logger logger = LoggerFactory.getLogger(DerbyService.class);

  /**
   * 获取连接
   * 
   * @return
   * @throws SQLException
   */
  protected Connection getConnection() throws SQLException {
    return DerbyLocalPoolManager.instance().getConn();
  }

  /**
   * 释放资源
   * 
   * @param conn
   * @param pst
   * @param rs
   */
  protected void release(Connection conn, PreparedStatement pst, ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (SQLException e) {
        logger.error("", e);
      }
    }

    if (pst != null) {
      try {
        pst.close();
      } catch (SQLException e) {
        logger.error("", e);
      }
    }

    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        logger.error("", e);
      }
    }
  }
}
