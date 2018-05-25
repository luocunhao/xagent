package xlink.core.derby;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Derby本地数据库连接池
 * @author shenweiran
 * create at 2017年4月14日 上午10:22:54
 */
public class DerbyLocalPoolManager {
	/**
	 * 数据源
	 */
	private final DataSource dataSource;
	
	private static final DerbyLocalPoolManager singleton = new DerbyLocalPoolManager();
	public static final DerbyLocalPoolManager instance(){
		return singleton;
	}
	private DerbyLocalPoolManager() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(org.apache.derby.jdbc.EmbeddedDriver.class.getName());
		ds.setUrl("jdbc:derby:derbyDB;create=true");
		ds.setUsername("admin");
		ds.setPassword("admin");
		ds.setInitialSize(2); // 初始的连接数；
		ds.setMaxTotal(50);
		ds.setMaxIdle(20);
		ds.setMinIdle(10);
		ds.setMaxWaitMillis(3000);
		dataSource = ds;
	}
	
	
	public Connection getConn() throws SQLException {
		Connection con = null;
		if (dataSource != null) {
			con= dataSource.getConnection();
		}
		return con;
	}
	
}
